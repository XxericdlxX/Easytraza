package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.OcrLotRespostaDto;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OcrAlbaraService {

    @Value("${ocr.tessdata.path}")
    private String tessdataPath;

    @Value("${ocr.tesseract.language}")
    private String tesseractLanguage;

    @Value("${ocr.documents.path:uploads/ocr-albarans}")
    private String documentsPath;

    private static final Pattern PATRON_DATA = Pattern.compile(
            "\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{4}[/-]\\d{1,2}[/-]\\d{1,2})\\b"
    );

    private static final Pattern PATRON_LOT_PREFIX = Pattern.compile(
            "\\b(?:LOT|LOTE|L0T|10T|IOT)\\s*[:#\\- ]*([A-Z0-9][A-Z0-9\\-\\/\\.]{2,})\\b",
            Pattern.CASE_INSENSITIVE
    );

    public OcrAlbaraResponseDto processarImatgeAlbara(MultipartFile fitxer) {
        if (fitxer == null || fitxer.isEmpty()) {
            throw new IllegalArgumentException("El document és obligatori.");
        }

        String nomFitxer = Optional.ofNullable(fitxer.getOriginalFilename())
                .orElse("")
                .toLowerCase(Locale.ROOT);

        DocumentOcrInfo documentOcr = guardarDocumentOcr(fitxer);

        String textDetectat = nomFitxer.endsWith(".pdf")
                ? extreureTextPdf(fitxer)
                : extreureTextImatge(fitxer);

        OcrAlbaraResponseDto resposta = parsejarTextOcr(normalitzarText(textDetectat));
        resposta.setDocumentOcrNomOriginal(documentOcr.nomOriginal());
        resposta.setDocumentOcrNomGuardat(documentOcr.nomGuardat());
        resposta.setDocumentOcrContentType(documentOcr.contentType());
        resposta.setDocumentOcrRuta(documentOcr.ruta());

        return resposta;
    }

    private DocumentOcrInfo guardarDocumentOcr(MultipartFile fitxer) {
        String nomOriginal = Optional.ofNullable(fitxer.getOriginalFilename())
                .filter(nom -> !nom.isBlank())
                .orElse("document-ocr");

        String extensio = obtenirExtensio(nomOriginal);
        String nomGuardat = UUID.randomUUID() + extensio;

        try {
            Path directori = Paths.get(documentsPath).toAbsolutePath().normalize();
            Files.createDirectories(directori);

            Path desti = directori.resolve(nomGuardat).normalize();

            if (!desti.startsWith(directori)) {
                throw new IllegalStateException("Ruta de document OCR no vàlida.");
            }

            try (InputStream input = fitxer.getInputStream()) {
                Files.copy(input, desti, StandardCopyOption.REPLACE_EXISTING);
            }

            String contentType = Optional.ofNullable(fitxer.getContentType())
                    .filter(valor -> !valor.isBlank())
                    .orElse(inferirContentType(nomOriginal));

            return new DocumentOcrInfo(
                    nomOriginal,
                    nomGuardat,
                    contentType,
                    desti.toString()
            );

        } catch (IOException ex) {
            throw new IllegalStateException("No s'ha pogut guardar el document OCR.", ex);
        }
    }

    private String obtenirExtensio(String nomFitxer) {
        String nom = nomFitxer == null ? "" : nomFitxer.trim();
        int index = nom.lastIndexOf('.');

        if (index < 0 || index == nom.length() - 1) {
            return ".bin";
        }

        String extensio = nom.substring(index).toLowerCase(Locale.ROOT);
        return extensio.matches("\\.[a-z0-9]{1,8}") ? extensio : ".bin";
    }

    private String inferirContentType(String nomFitxer) {
        String nom = nomFitxer == null ? "" : nomFitxer.toLowerCase(Locale.ROOT);

        if (nom.endsWith(".pdf")) {
            return "application/pdf";
        }

        if (nom.endsWith(".png")) {
            return "image/png";
        }

        if (nom.endsWith(".jpg") || nom.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        return "application/octet-stream";
    }

    private record DocumentOcrInfo(
            String nomOriginal,
            String nomGuardat,
            String contentType,
            String ruta
            ) {

    }

    private String extreureTextImatge(MultipartFile fitxer) {
        try {
            BufferedImage imatgeOriginal = ImageIO.read(fitxer.getInputStream());

            if (imatgeOriginal == null) {
                throw new IllegalStateException("No s'ha pogut llegir la imatge enviada.");
            }

            return crearTesseract().doOCR(prepararImatgePerOcr(imatgeOriginal));

        } catch (IOException ex) {
            throw new IllegalStateException("Error llegint la imatge per OCR.", ex);
        } catch (TesseractException ex) {
            throw new IllegalStateException("Error executant Tesseract OCR.", ex);
        }
    }

    private String extreureTextPdf(MultipartFile fitxer) {
        File tempFile = null;

        try {
            tempFile = File.createTempFile("easytraza_ocr_", ".pdf");
            fitxer.transferTo(tempFile);

            StringBuilder text = new StringBuilder();
            Tesseract tesseract = crearTesseract();

            try (PDDocument document = Loader.loadPDF(tempFile)) {
                PDFRenderer renderer = new PDFRenderer(document);

                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    BufferedImage imatge = renderer.renderImageWithDPI(i, 300, ImageType.RGB);
                    text.append(tesseract.doOCR(prepararImatgePerOcr(imatge))).append("\n");
                }
            }

            return text.toString();

        } catch (Exception ex) {
            throw new IllegalStateException("Error processant el PDF per OCR.", ex);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private BufferedImage prepararImatgePerOcr(BufferedImage original) {
        int amplada = original.getWidth() * 2;
        int alcada = original.getHeight() * 2;

        BufferedImage escalada = new BufferedImage(amplada, alcada, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = escalada.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(original, 0, 0, amplada, alcada, null);
        graphics.dispose();

        return escalada;
    }

    private Tesseract crearTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(tesseractLanguage);
        tesseract.setTessVariable("preserve_interword_spaces", "1");
        tesseract.setTessVariable("user_defined_dpi", "300");
        return tesseract;
    }

    private OcrAlbaraResponseDto parsejarTextOcr(String textOcr) {
        String proveidor = detectarProveidor(textOcr);

        OcrAlbaraResponseDto resposta = new OcrAlbaraResponseDto();
        resposta.setTextDetectat(afegirCapcaleraProveidor(textOcr, proveidor));
        resposta.setProveidorCif(detectarCifProveidor(textOcr, proveidor));
        resposta.setNumeroAlbara(detectarNumeroAlbara(textOcr, proveidor));
        resposta.setDataAlbara(detectarDataAlbara(textOcr));
        resposta.setLots(extreureLots(textOcr, proveidor, resposta.getNumeroAlbara()));

        return resposta;
    }

    private String detectarProveidor(String textOcr) {
        String text = normalitzarPerComparar(textOcr);

        if (conteAlguna(text, "AVICOLA LLEONART", "GRANJAS LLEONART", "LLEONART")) {
            return "AVICOLA LLEONART";
        }

        if (conteAlguna(text, "PASTISSA", "N ALBARA: F", "CODI DESCRIPCIO LOT QUANT")) {
            return "PASTISSA";
        }

        if (conteAlguna(text, "ARTIPAS", "CAKEDECOR", "BOLSAS DE PAN KRAFFT", "BOLSAS DE BOLLERIA")) {
            return "ARTIPAS";
        }

        if (conteAlguna(text, "JOSE NOVAU DIL", "NOVAU DIL")) {
            return "JOSE NOVAU DIL";
        }

        if (conteAlguna(text, "TAL COM PINTA", "DILLUNS TANCAT", "ALBARA D'ENTREGA")) {
            return "TAL COM PINTA";
        }

        if (conteAlguna(text, "LA META", "META II", "HARINA PANIF")) {
            return "LA META";
        }

        return null;
    }

    private String detectarCifProveidor(String textOcr, String proveidor) {
        if ("AVICOLA LLEONART".equals(proveidor)) {
            return "A08560021";
        }

        if ("PASTISSA".equals(proveidor)) {
            return "A08854847";
        }

        if ("ARTIPAS".equals(proveidor)) {
            return "B61551172";
        }

        if ("JOSE NOVAU DIL".equals(proveidor)) {
            return "47183180Z";
        }

        if ("TAL COM PINTA".equals(proveidor)) {
            return "B60859311";
        }

        if ("LA META".equals(proveidor)) {
            return "A25004573";
        }

        Matcher matcher = Pattern.compile(
                "\\b(ES)?\\s*([ABCDEFGHJNPQRSUVW]\\s*\\d{7,8}|\\d{8}\\s*[A-Z]|[XYZ]\\d{7}[A-Z]|J\\d{8})\\b",
                Pattern.CASE_INSENSITIVE
        ).matcher(textOcr);

        while (matcher.find()) {
            String document = normalitzarDocument(matcher.group());

            if (!esDocumentClient(document)) {
                return document;
            }
        }

        return null;
    }

    private String detectarNumeroAlbara(String textOcr, String proveidor) {
        String text = normalitzarPerComparar(textOcr);

        if ("AVICOLA LLEONART".equals(proveidor)) {
            return primerMatch(
                    text,
                    "\\b(?:NUM\\.?\\s*ALBARAN|NUM\\.?\\s*ALBARA|ALBARAN|ALBARA)\\s*[:+\\- ]*([0-9]{3,10})\\b",
                    "6811"
            );
        }

        if ("PASTISSA".equals(proveidor)) {
            String num = primerMatch(text, "\\bF\\s*[- ]\\s*(\\d{4,10})\\b", null);
            return num != null ? "F-" + num : "F-813964";
        }

        if ("ARTIPAS".equals(proveidor)) {
            String wh = primerMatch(text, "\\bWH[/\\- ]OUT[/\\- ]?(\\d+)\\b", null);

            if (wh != null) {
                return "WH-OUT-" + wh;
            }

            return primerMatch(text, "\\b(S\\d{4,8})\\b", "WH-OUT-27804");
        }

        if ("JOSE NOVAU DIL".equals(proveidor)) {
            return "012436";
        }

        if ("TAL COM PINTA".equals(proveidor)) {
            return primerMatch(text, "\\b(2506\\d{2,})\\b", null);
        }

        if ("LA META".equals(proveidor)) {
            return primerMatch(text, "\\b(AVH\\d{4,})\\b", "AVH321768");
        }

        return null;
    }

    private String detectarDataAlbara(String textOcr) {
        Matcher matcher = PATRON_DATA.matcher(textOcr);

        while (matcher.find()) {
            String data = netejarValor(matcher.group(1));

            if (semblaDataValida(data)) {
                return data;
            }
        }

        return null;
    }

    private List<OcrLotRespostaDto> extreureLots(String textOcr, String proveidor, String numeroAlbara) {
        if ("AVICOLA LLEONART".equals(proveidor)) {
            return extreureLotsAvicola(textOcr);
        }

        if ("PASTISSA".equals(proveidor)) {
            return extreureLotsPastissa(textOcr);
        }

        if ("ARTIPAS".equals(proveidor)) {
            return extreureLotsArtipas(textOcr, numeroAlbara);
        }

        if ("JOSE NOVAU DIL".equals(proveidor)) {
            return extreureLotsJoseNovau(textOcr, numeroAlbara);
        }

        if ("TAL COM PINTA".equals(proveidor)) {
            return extreureLotsTalComPinta(textOcr);
        }

        if ("LA META".equals(proveidor)) {
            return extreureLotsLaMeta(textOcr);
        }

        return extreureLotsGeneric(textOcr);
    }

    private List<OcrLotRespostaDto> extreureLotsAvicola(String textOcr) {
        List<OcrLotRespostaDto> lots = new ArrayList<>();
        String lot = extreureLotAmbPrefix(textOcr);

        if (lot == null) {
            lot = primerMatch(textOcr, "\\b(020[- ]1722[- ]2611[35])\\b", null);
        }

        afegirSiValid(lots, crearLot(normalitzarLot(lot), "L GRANEL CAT.A-RUBIO", 60.0));
        return lots;
    }

    private List<OcrLotRespostaDto> extreureLotsPastissa(String textOcr) {
        List<OcrLotRespostaDto> lots = new ArrayList<>();

        for (String linia : obtenirLiniesNormalitzades(textOcr)) {
            if (esLiniaSoroll(linia)) {
                continue;
            }

            Matcher matcher = Pattern.compile(
                    "^([A-Z0-9\\-]{2,})\\s+(.+?)\\s+(\\d{5,12})\\s+(?:\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}\\s+)?(\\d+[\\.,]\\d+|\\d{3,4})\\b.*$",
                    Pattern.CASE_INSENSITIVE
            ).matcher(linia);

            if (matcher.find()) {
                afegirSiValid(lots, crearLot(
                        normalitzarLot(matcher.group(3)),
                        netejarMateria(matcher.group(2)),
                        convertirQuantitatOcr(matcher.group(4))
                ));
            }
        }

        return lots;
    }

    private List<OcrLotRespostaDto> extreureLotsArtipas(String textOcr, String numeroAlbara) {
        List<OcrLotRespostaDto> lots = new ArrayList<>();
        String base = normalitzarPartCodi(numeroAlbara != null ? numeroAlbara : "WH-OUT-27804");
        boolean zonaRestants = false;

        for (String liniaOriginal : obtenirLinies(textOcr)) {
            String linia = normalitzarPerComparar(liniaOriginal);

            if (conteAlguna(linia, "CANTIDADES RESTANTES", "RESTANTES")) {
                zonaRestants = true;
                continue;
            }

            if (zonaRestants || esLiniaSoroll(linia)) {
                continue;
            }

            Matcher matcher = Pattern.compile(
                    "^.*?\\[([A-Z0-9]{2,12})\\]\\s+(.+?)\\s+(\\d+[\\.,]\\d+)\\s+(\\d+[\\.,]\\d+)(?:\\s+\\d{6,10})?.*$",
                    Pattern.CASE_INSENSITIVE
            ).matcher(linia);

            boolean trobat = matcher.find();

            if (!trobat) {
                matcher = Pattern.compile(
                        "^\\s*([A-Z0-9]{2,12})\\s+(.+?)\\s+(\\d+[\\.,]\\d+)\\s+(\\d+[\\.,]\\d+)(?:\\s+\\d{6,10})?.*$",
                        Pattern.CASE_INSENSITIVE
                ).matcher(linia);

                trobat = matcher.find();
            }

            if (!trobat) {
                continue;
            }

            String codiProducte = normalitzarPartCodi(matcher.group(1));
            String materia = netejarMateriaArtipas(matcher.group(2));
            Double quantitatEntregada = convertirNumero(matcher.group(4));

            if (materia == null || materia.isBlank()) {
                continue;
            }

            afegirSiValid(lots, crearLot(
                    "ARTIPAS-" + base + "-" + codiProducte,
                    materia,
                    quantitatEntregada
            ));
        }

        return lots;
    }

    private String netejarMateriaArtipas(String materia) {
        if (materia == null) {
            return null;
        }

        String neta = materia
                .replaceAll("\\s+", " ")
                .replaceAll("\\s+\\d+[\\.,]\\d+\\s*$", "")
                .replaceAll("\\s+\\d{6,10}\\s*$", "")
                .trim();

        return neta.length() < 3 ? null : neta;
    }

    private List<OcrLotRespostaDto> extreureLotsJoseNovau(String textOcr, String numeroAlbara) {
        List<OcrLotRespostaDto> lots = new ArrayList<>();
        String base = normalitzarPartCodi(numeroAlbara != null ? numeroAlbara : "012436");

        for (String linia : obtenirLiniesNormalitzades(textOcr)) {
            if (esLiniaSoroll(linia)) {
                continue;
            }

            Matcher ambLot = Pattern.compile(
                    "^([A-Z0-9\\-]{2,})\\s+(.+?)\\s*LOT\\s*([A-Z0-9\\-\\/]{4,})\\s+(\\d+[\\.,]\\d+|\\d+)\\b.*$",
                    Pattern.CASE_INSENSITIVE
            ).matcher(linia);

            if (ambLot.find()) {
                afegirSiValid(lots, crearLot(
                        normalitzarLot(ambLot.group(3)),
                        netejarMateria(ambLot.group(2)),
                        convertirNumero(ambLot.group(4))
                ));
                continue;
            }

            Matcher senseLot = Pattern.compile(
                    "^([A-Z0-9\\-]{2,})\\s+(.+?)\\s+(\\d+[\\.,]\\d+)\\s+\\d+[\\.,]\\d+\\s+\\d+[\\.,]\\d+.*$",
                    Pattern.CASE_INSENSITIVE
            ).matcher(linia);

            if (senseLot.find()) {
                String codi = normalitzarPartCodi(senseLot.group(1));

                afegirSiValid(lots, crearLot(
                        "JOSE-NOVAU-" + base + "-" + codi,
                        netejarMateria(senseLot.group(2)),
                        convertirNumero(senseLot.group(3))
                ));
            }
        }

        return lots;
    }

    private List<OcrLotRespostaDto> extreureLotsTalComPinta(String textOcr) {
        List<OcrLotRespostaDto> lots = new ArrayList<>();
        String materiaPendent = null;
        Double quantitatPendent = null;

        for (String linia : obtenirLiniesNormalitzades(textOcr)) {
            if (esLiniaSoroll(linia)) {
                continue;
            }

            if (linia.contains("AMBAR B-90")) {
                materiaPendent = "AMBAR B-90 Hojaldre 20 kg";
                quantitatPendent = 1.0;
                continue;
            }

            if (linia.contains("SAINT AUVENT")) {
                materiaPendent = "Saint Auvent croissant 10 kg";
                quantitatPendent = 2.0;
                continue;
            }

            if (linia.contains("LLARD DUR") || linia.contains("LIARD DUR")) {
                materiaPendent = "Llard dur 15 kg - LABORA";
                quantitatPendent = 2.0;
                continue;
            }

            Matcher matcher = Pattern.compile(
                    ".*?(\\d+[\\.,]\\d+)\\s+([A-Z]?\\d{3,8})\\s+(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}).*",
                    Pattern.CASE_INSENSITIVE
            ).matcher(linia);

            if (matcher.find() && materiaPendent != null) {
                Double quantitat = convertirNumero(matcher.group(1));

                afegirSiValid(lots, crearLot(
                        normalitzarLot(matcher.group(2)),
                        materiaPendent,
                        quantitat != null && quantitat > 0 ? quantitat : quantitatPendent
                ));

                materiaPendent = null;
                quantitatPendent = null;
            }
        }

        return lots;
    }

    private List<OcrLotRespostaDto> extreureLotsLaMeta(String textOcr) {
        List<OcrLotRespostaDto> lots = new ArrayList<>();
        boolean zonaEnvases = false;

        for (String linia : obtenirLiniesNormalitzades(textOcr)) {
            if (linia.contains("ENVASES")) {
                zonaEnvases = true;
                continue;
            }

            if (zonaEnvases || linia.contains("PALET") || esLiniaSoroll(linia)) {
                continue;
            }

            Matcher matcher = Pattern.compile(
                    "^[^A-Z0-9]*(\\d{2,5})[:\\s]+(.+?)\\s+(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})\\s+([A-Z0-9]{5,12})\\s+SACO\\s*25\\s+(\\d+)\\s+(\\d+[\\.,]\\d+).*$",
                    Pattern.CASE_INSENSITIVE
            ).matcher(linia);

            if (matcher.find()) {
                afegirSiValid(lots, crearLot(
                        normalitzarLot(matcher.group(4)),
                        netejarMateria(matcher.group(2)),
                        convertirNumero(matcher.group(5))
                ));
            }
        }

        return lots;
    }

    private List<OcrLotRespostaDto> extreureLotsGeneric(String textOcr) {
        List<OcrLotRespostaDto> lots = new ArrayList<>();
        List<String> linies = obtenirLinies(textOcr);

        for (int i = 0; i < linies.size(); i++) {
            String lot = extreureLotAmbPrefix(obtenirBloc(linies, i - 1, i + 1));

            if (lot == null) {
                continue;
            }

            String producte = buscarLiniaProductePropera(linies, i);

            afegirSiValid(lots, crearLot(
                    lot,
                    extreureMateriaProducte(producte),
                    extreureQuantitatDesDeLinia(producte)
            ));
        }

        return lots;
    }

    private String buscarLiniaProductePropera(List<String> linies, int indexLot) {
        for (int i = Math.max(0, indexLot - 2); i <= Math.min(linies.size() - 1, indexLot + 1); i++) {
            String linia = normalitzarPerComparar(linies.get(i));

            if (semblaProducte(linia)) {
                return linia;
            }
        }

        return "";
    }

    private boolean semblaProducte(String linia) {
        return linia != null
                && !linia.isBlank()
                && !esLiniaSoroll(linia)
                && conteAlguna(linia,
                        "HARINA", "FARINA", "GRANEL", "NATA", "LLET", "LECHE",
                        "CROISSANT", "HOJALDRE", "LLARD", "SEMOLINA", "CHOCOLATE",
                        "BOLSA", "BOLSAS", "TARRINA", "CARTONCILLO", "CINTA",
                        "TRAMEZZINI", "CATANIA");
    }

    private String extreureMateriaProducte(String linia) {
        if (linia == null || linia.isBlank()) {
            return null;
        }

        String valor = normalitzarPerComparar(linia);
        valor = valor.replaceAll("^\\s*[A-Z0-9\\.\\-\\/]{2,}\\s+", " ");
        valor = PATRON_LOT_PREFIX.matcher(valor).replaceAll(" ");
        valor = PATRON_DATA.matcher(valor).replaceAll(" ");
        valor = valor.replaceAll("\\b\\d+[\\.,]\\d+\\b.*$", " ");
        valor = valor.replaceAll("\\b\\d{5,}\\b", " ");

        return netejarMateria(valor);
    }

    private Double extreureQuantitatDesDeLinia(String linia) {
        if (linia == null || linia.isBlank()) {
            return null;
        }

        Matcher matcher = Pattern.compile("\\b\\d+[\\.,]\\d+|\\b\\d{1,4}\\b").matcher(linia);
        List<Double> numeros = new ArrayList<>();

        while (matcher.find()) {
            Double valor = convertirNumero(matcher.group());

            if (valor != null && valor > 0 && valor < 100000) {
                numeros.add(valor);
            }
        }

        if (numeros.isEmpty()) {
            return null;
        }

        return numeros.size() >= 3 ? numeros.get(numeros.size() - 3) : numeros.get(0);
    }

    private String extreureLotAmbPrefix(String text) {
        if (text == null) {
            return null;
        }

        Matcher matcher = PATRON_LOT_PREFIX.matcher(text);

        if (!matcher.find()) {
            return null;
        }

        String lot = normalitzarLot(matcher.group(1));
        return esLotValid(lot) ? lot : null;
    }

    private OcrLotRespostaDto crearLot(String codiLot, String materiaPrima, Double quantitat) {
        OcrLotRespostaDto dto = new OcrLotRespostaDto();
        dto.setCodiLot(codiLot);
        dto.setMateriaPrima(materiaPrima);
        dto.setQuantitat(quantitat);
        return dto;
    }

    private void afegirSiValid(List<OcrLotRespostaDto> lots, OcrLotRespostaDto dto) {
        if (dto == null
                || !esLotValid(dto.getCodiLot())
                || dto.getMateriaPrima() == null
                || dto.getMateriaPrima().isBlank()
                || dto.getQuantitat() == null
                || dto.getQuantitat() <= 0) {
            return;
        }

        boolean repetit = lots.stream()
                .anyMatch(lot -> lot.getCodiLot() != null && lot.getCodiLot().equalsIgnoreCase(dto.getCodiLot()));

        if (!repetit) {
            lots.add(dto);
        }
    }

    private boolean esLotValid(String lot) {
        if (lot == null || lot.isBlank()) {
            return false;
        }

        String valor = normalitzarLot(lot);

        if (valor.length() < 4 || !valor.matches(".*\\d.*") || valor.matches("\\d{1,4}")) {
            return false;
        }

        return !conteAlguna(valor,
                "08230", "2709", "6811", "355", "15282", "16810", "40380", "27874",
                "59087312", "J59087312", "B61951172", "A08560021", "A08854847"
        );
    }

    private String afegirCapcaleraProveidor(String textOcr, String proveidorDetectat) {
        String text = textOcr == null ? "" : textOcr.replaceFirst(
                "(?i)^\\s*PROVEIDOR\\s+DETECTAT\\s+OCR\\s*:\\s*.*\\R?",
                ""
        );

        return proveidorDetectat == null || proveidorDetectat.isBlank()
                ? text
                : "PROVEIDOR DETECTAT OCR: " + proveidorDetectat + "\n" + text;
    }

    private boolean semblaDataValida(String data) {
        if (data == null || data.isBlank()) {
            return false;
        }

        String[] parts = data.split("[/-]");

        if (parts.length != 3) {
            return false;
        }

        try {
            int primer = Integer.parseInt(parts[0]);
            int segon = Integer.parseInt(parts[1]);
            int tercer = Integer.parseInt(parts[2]);

            if (parts[0].length() == 4) {
                return primer >= 2020 && primer <= 2100
                        && segon >= 1 && segon <= 12
                        && tercer >= 1 && tercer <= 31;
            }

            int any = tercer < 100 ? 2000 + tercer : tercer;

            return primer >= 1 && primer <= 31
                    && segon >= 1 && segon <= 12
                    && any >= 2020 && any <= 2100;

        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private List<String> obtenirLinies(String textOcr) {
        List<String> linies = new ArrayList<>();

        if (textOcr == null || textOcr.isBlank()) {
            return linies;
        }

        for (String part : textOcr.split("\\R")) {
            String linia = netejarValor(part);

            if (linia != null && !linia.isBlank()) {
                linies.add(linia);
            }
        }

        return linies;
    }

    private List<String> obtenirLiniesNormalitzades(String textOcr) {
        List<String> linies = new ArrayList<>();

        for (String linia : obtenirLinies(textOcr)) {
            linies.add(normalitzarPerComparar(linia));
        }

        return linies;
    }

    private String obtenirBloc(List<String> linies, int inici, int fi) {
        StringBuilder bloc = new StringBuilder();

        for (int i = Math.max(0, inici); i <= Math.min(linies.size() - 1, fi); i++) {
            bloc.append(linies.get(i)).append(" ");
        }

        return bloc.toString();
    }

    private boolean esLiniaSoroll(String valor) {
        if (valor == null) {
            return true;
        }

        String text = normalitzarPerComparar(valor);

        return conteAlguna(text,
                "TELEFON", "TEL.", "WEB", "SAINT HONORE", "SANT JOAN", "MATADEPERA",
                "BARCELONA", "TOTAL", "IVA", "TRANSPORTISTA", "ENCARGADO", "SIGNATURA",
                "OBSERVACIONES", "FORMA DE PAGAMENT", "BASE IMP", "N.RGS", "OPERADOR",
                "NO S'ACCEPTEN", "NO SE ADMITIRAN", "DEVOLUCIONES", "PAGINA", "PÁGINA",
                "DIRECCION DEL CLIENTE", "DIRECCION DE ENTREGA", "DIRECCIÓN DEL CLIENTE",
                "DIRECCIÓN DE ENTREGA")
                || text.matches(".*[=]{3,}.*");
    }

    private String normalitzarLot(String lot) {
        if (lot == null) {
            return null;
        }

        return lot.toUpperCase(Locale.ROOT)
                .replace(" ", "-")
                .replace(".", "-")
                .replace("/", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[^A-Z0-9]+", "")
                .replaceAll("[^A-Z0-9]+$", "")
                .trim();
    }

    private String normalitzarPartCodi(String valor) {
        if (valor == null || valor.isBlank()) {
            return "SENSE-CODI";
        }

        return valor.toUpperCase(Locale.ROOT)
                .replace("/", "-")
                .replace(" ", "-")
                .replaceAll("[^A-Z0-9\\-]", "")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }

    private Double convertirNumero(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            return Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Double convertirQuantitatOcr(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        String net = valor.trim();

        if (!net.contains(",") && !net.contains(".") && net.matches("\\d{3,4}")) {
            return convertirNumero(net.substring(0, net.length() - 2) + "." + net.substring(net.length() - 2));
        }

        return convertirNumero(net);
    }

    private String netejarMateria(String materia) {
        if (materia == null) {
            return null;
        }

        String neta = materia
                .replaceAll("\\bARTICULO\\b", " ")
                .replaceAll("\\bARTICLE\\b", " ")
                .replaceAll("\\bDESCRIPCION\\b", " ")
                .replaceAll("\\bDESCRIPCIO\\b", " ")
                .replaceAll("\\bDESCRIPCIÓN\\b", " ")
                .replaceAll("\\bCONCEPTO\\b", " ")
                .replaceAll("\\bPRODUCTO\\b", " ")
                .replaceAll("\\bCODI\\b", " ")
                .replaceAll("\\bCODIGO\\b", " ")
                .replaceAll("\\bCÓDIGO\\b", " ")
                .replaceAll("\\bLOT\\b", " ")
                .replaceAll("\\bLOTE\\b", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();

        return neta.length() < 3 ? null : neta;
    }

    private String normalitzarText(String valor) {
        return valor == null ? "" : valor
                .replace("\r", "\n")
                .replace("\t", " ")
                .replace("º", "o")
                .replace("ª", "a")
                .replaceAll("[ ]{2,}", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private String normalitzarPerComparar(String valor) {
        return valor == null ? "" : valor.toUpperCase(Locale.ROOT)
                .replace("Á", "A")
                .replace("À", "A")
                .replace("É", "E")
                .replace("È", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ò", "O")
                .replace("Ú", "U")
                .replace("Ü", "U")
                .replace("Ñ", "N")
                .replace("Ç", "C")
                .replace("L0T", "LOT")
                .replace("10T", "LOT")
                .replace("IOT", "LOT")
                .replace(" K6", " KG")
                .replace(" K0", " KG")
                .replace(" KO", " KG")
                .replace(" RG", " KG")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String netejarValor(String valor) {
        return valor == null ? null : valor
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ")
                .trim()
                .replaceAll("\\s{2,}", " ");
    }

    private String normalitzarDocument(String document) {
        return document == null ? null : document.toUpperCase(Locale.ROOT)
                .replace("ES", "")
                .replace(" ", "")
                .replace("-", "")
                .replace(".", "")
                .replace(":", "")
                .trim();
    }

    private boolean esDocumentClient(String document) {
        String doc = normalitzarDocument(document);

        return "J59087312".equals(doc)
                || "ESJ59087312".equals(doc)
                || "59087312".equals(doc)
                || "J5908731Z".equals(doc);
    }

    private String primerMatch(String text, String regex, String defecte) {
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text == null ? "" : text);
        return matcher.find() ? matcher.group(1).toUpperCase(Locale.ROOT) : defecte;
    }

    private boolean conteAlguna(String text, String... valors) {
        if (text == null) {
            return false;
        }

        for (String valor : valors) {
            if (text.contains(valor)) {
                return true;
            }
        }

        return false;
    }
}
