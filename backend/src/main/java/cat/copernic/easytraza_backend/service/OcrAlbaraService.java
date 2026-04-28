package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.OcrLotRespostaDto;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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

    private static final Pattern PATRON_CIF_NIF = Pattern.compile(
            "\\b(ES)?([A-Z]\\d{7,8}|\\d{8}[A-Z]|[XYZ]\\d{7}[A-Z])\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATRON_DATA = Pattern.compile(
            "\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{4}[/-]\\d{1,2}[/-]\\d{1,2})\\b"
    );

    private static final Pattern PATRON_NUMERO_ALBARA = Pattern.compile(
            "\\b(?:N[ºO]?\\s*ALBARA|N[ºO]?\\s*ALBARAN|NUM\\.?\\s*ALBARAN|NUM\\.?\\s*ALBARA|ALBARA|ALBARAN|ALBARÀ|DOCUMENT|N\\.\\s*ENTREGA|ORDEN)\\s*[:#\\- ]*([A-Z0-9][A-Z0-9\\-\\/ ]{2,})\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATRON_LOT = Pattern.compile(
            "\\b(?:LOT|LOTE|L0T|10T|IOT)\\s*[:#\\- ]*([A-Z0-9][A-Z0-9\\-\\/\\.]{2,})\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATRON_LOT_SENSE_PREFIX = Pattern.compile(
            "\\b([A-Z]{1,4}\\d{3,}[A-Z0-9\\-\\/]*|\\d{5,}[A-Z0-9\\-\\/]*|[A-Z0-9]{4,}[-\\/][A-Z0-9]{2,})\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATRON_QUANTITAT = Pattern.compile(
            "\\b(\\d+(?:[\\.,]\\d+)?)\\s*(KG|KGS|G|GR|L|ML|UD|UDS|U|SACO|SACOS|TONELADAS|TONA|TONES)?\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final List<String> PROVEIDORS_CONEGUTS = List.of(
            "PASTISSA",
            "JOSE NOVAU DIL",
            "AVICOLA LLEONART",
            "AVÍCOLA LLEONART",
            "LLEONART",
            "ARTIPAS",
            "TAL COM PINTA",
            "LA META"
    );

    public OcrAlbaraResponseDto processarImatgeAlbara(MultipartFile fitxer) {
        if (fitxer == null || fitxer.isEmpty()) {
            throw new IllegalArgumentException("El document és obligatori.");
        }

        String nomFitxer = Optional.ofNullable(fitxer.getOriginalFilename())
                .orElse("")
                .toLowerCase(Locale.ROOT);

        String textDetectat;

        if (nomFitxer.endsWith(".pdf")) {
            textDetectat = extreureTextPdf(fitxer);
        } else {
            textDetectat = extreureTextImatge(fitxer);
        }

        return parsejarTextOcr(normalitzarText(textDetectat));
    }

    private String extreureTextImatge(MultipartFile fitxer) {
        try {
            BufferedImage imatgeOriginal = ImageIO.read(fitxer.getInputStream());

            if (imatgeOriginal == null) {
                throw new IllegalStateException("No s'ha pogut llegir la imatge enviada.");
            }

            BufferedImage imatgePreparada = prepararImatgePerOcr(imatgeOriginal);
            Tesseract tesseract = crearTesseract();

            return tesseract.doOCR(imatgePreparada);

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
                    BufferedImage imatgePreparada = prepararImatgePerOcr(imatge);
                    text.append(tesseract.doOCR(imatgePreparada)).append("\n");
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
        OcrAlbaraResponseDto resposta = new OcrAlbaraResponseDto();

        String proveidorDetectat = detectarProveidorConegut(textOcr);
        String textAmbProveidor = afegirProveidorDetectatAlText(textOcr, proveidorDetectat);

        resposta.setTextDetectat(textAmbProveidor);
        resposta.setProveidorCif(extreurePrimer(textOcr, PATRON_CIF_NIF));
        resposta.setNumeroAlbara(extreureNumeroAlbara(textOcr));
        resposta.setDataAlbara(extreureData(textOcr));
        resposta.setLots(extreureLots(textOcr));

        return resposta;
    }

    private List<OcrLotRespostaDto> extreureLots(String textOcr) {
        List<OcrLotRespostaDto> lots = new ArrayList<>();
        List<String> linies = obtenirLinies(textOcr);

        for (int i = 0; i < linies.size(); i++) {
            String linia = linies.get(i);

            if (!semblaLiniaDeProducte(linia)) {
                continue;
            }

            String materia = extreureMateria(linia);
            String lot = extreureLot(linia);
            Double quantitat = extreureQuantitat(linia);

            if (lot == null) {
                lot = cercarLotProper(linies, i);
            }

            if (quantitat == null) {
                quantitat = cercarQuantitatPropera(linies, i);
            }

            if (materia == null || materia.isBlank()) {
                continue;
            }

            OcrLotRespostaDto lotDto = new OcrLotRespostaDto();
            lotDto.setMateriaPrima(materia);
            lotDto.setCodiLot(lot);
            lotDto.setQuantitat(quantitat);

            lots.add(lotDto);
        }

        if (lots.isEmpty()) {
            OcrLotRespostaDto fallback = crearLotFallback(linies);

            if (fallback != null) {
                lots.add(fallback);
            }
        }

        return lots;
    }

    private OcrLotRespostaDto crearLotFallback(List<String> linies) {
        String materia = null;
        String lot = null;
        Double quantitat = null;

        for (String linia : linies) {
            if (materia == null && semblaLiniaDeProducte(linia)) {
                materia = extreureMateria(linia);
            }

            if (lot == null) {
                lot = extreureLot(linia);
            }

            if (quantitat == null) {
                quantitat = extreureQuantitat(linia);
            }
        }

        if (materia == null && lot == null && quantitat == null) {
            return null;
        }

        OcrLotRespostaDto dto = new OcrLotRespostaDto();
        dto.setMateriaPrima(materia);
        dto.setCodiLot(lot);
        dto.setQuantitat(quantitat);
        return dto;
    }

    private String detectarProveidorConegut(String textOcr) {
        String text = normalitzarPerComparar(textOcr);

        if (text.contains("PASTISSA") || text.contains("PAST1SSA") || text.contains("PASTI5SA")) {
            return "PASTISSA";
        }

        if (text.contains("JOSE NOVAU DIL") || text.contains("JOSENOVAU") || text.contains("NOVAU DIL")) {
            return "JOSE NOVAU DIL";
        }

        if (text.contains("AVICOLA LLEONART") || text.contains("LLEONART")) {
            return "AVICOLA LLEONART";
        }

        if (text.contains("ARTIPAS")) {
            return "ARTIPAS";
        }

        if (text.contains("TAL COM PINTA") || text.contains("TALCOMPINTA")) {
            return "TAL COM PINTA";
        }

        if (text.contains("LA META") || text.contains("META II") || text.contains("MHM")) {
            return "LA META";
        }

        for (String proveidor : PROVEIDORS_CONEGUTS) {
            if (text.contains(normalitzarPerComparar(proveidor))) {
                return proveidor;
            }
        }

        return null;
    }

    private String afegirProveidorDetectatAlText(String textOcr, String proveidorDetectat) {
        if (proveidorDetectat == null || proveidorDetectat.isBlank()) {
            return textOcr;
        }

        return "PROVEIDOR DETECTAT OCR: " + proveidorDetectat + "\n" + textOcr;
    }

    private String extreureNumeroAlbara(String textOcr) {
        String numero = extreurePrimer(textOcr, PATRON_NUMERO_ALBARA);

        if (numero != null) {
            return netejarValor(numero);
        }

        Matcher whOut = Pattern.compile("\\b(WH/OUT/\\d+)\\b", Pattern.CASE_INSENSITIVE).matcher(textOcr);
        if (whOut.find()) {
            return whOut.group(1).toUpperCase(Locale.ROOT);
        }

        Matcher avh = Pattern.compile("\\b(AVH\\d+)\\b", Pattern.CASE_INSENSITIVE).matcher(textOcr);
        if (avh.find()) {
            return avh.group(1).toUpperCase(Locale.ROOT);
        }

        return null;
    }

    private String extreureData(String textOcr) {
        Matcher matcher = PATRON_DATA.matcher(textOcr);

        while (matcher.find()) {
            String data = netejarValor(matcher.group(1));

            if (semblaDataValida(data)) {
                return data;
            }
        }

        return null;
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
                return primer >= 2020 && primer <= 2100 && segon >= 1 && segon <= 12 && tercer >= 1 && tercer <= 31;
            }

            int any = tercer < 100 ? 2000 + tercer : tercer;
            return primer >= 1 && primer <= 31 && segon >= 1 && segon <= 12 && any >= 2020 && any <= 2100;

        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private String extreureLot(String linia) {
        String lotAmbPrefix = extreurePrimer(linia, PATRON_LOT);

        if (lotAmbPrefix != null) {
            return normalitzarLot(lotAmbPrefix);
        }

        Matcher matcher = PATRON_LOT_SENSE_PREFIX.matcher(linia);

        while (matcher.find()) {
            String candidat = normalitzarLot(matcher.group(1));

            if (esLotValid(candidat)) {
                return candidat;
            }
        }

        return null;
    }

    private String cercarLotProper(List<String> linies, int index) {
        for (int i = index; i <= index + 2 && i < linies.size(); i++) {
            String lot = extreureLot(linies.get(i));

            if (lot != null) {
                return lot;
            }
        }

        return null;
    }

    private Double extreureQuantitat(String linia) {
        Matcher matcher = PATRON_QUANTITAT.matcher(normalitzarPerComparar(linia));
        List<Double> numeros = new ArrayList<>();

        while (matcher.find()) {
            String valor = matcher.group(1);

            try {
                double numero = Double.parseDouble(valor.replace(",", "."));

                if (numero > 0 && numero < 100000) {
                    numeros.add(numero);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        if (numeros.isEmpty()) {
            return null;
        }

        return numeros.get(numeros.size() - 1);
    }

    private Double cercarQuantitatPropera(List<String> linies, int index) {
        for (int i = index; i <= index + 2 && i < linies.size(); i++) {
            Double quantitat = extreureQuantitat(linies.get(i));

            if (quantitat != null) {
                return quantitat;
            }
        }

        return null;
    }

    private String extreureMateria(String linia) {
        String valor = normalitzarPerComparar(linia);

        valor = PATRON_CIF_NIF.matcher(valor).replaceAll(" ");
        valor = PATRON_DATA.matcher(valor).replaceAll(" ");
        valor = PATRON_LOT.matcher(valor).replaceAll(" ");
        valor = PATRON_LOT_SENSE_PREFIX.matcher(valor).replaceAll(" ");
        valor = PATRON_QUANTITAT.matcher(valor).replaceAll(" ");

        valor = valor.replaceAll("(?i)\\b(CODI|CODIGO|CÓDIGO|ARTICLE|ARTICULO|ARTÍCULO|PRODUCTO|CONCEPTO|DESCRIPCIO|DESCRIPCION|DESCRIPCIÓN)\\b", " ");
        valor = valor.replaceAll("(?i)\\b(LOT|LOTE|QUANT|QUANTITAT|CANTIDAD|PREU|PRECIO|IMPORT|IVA|DTE|TOTAL)\\b", " ");
        valor = valor.replaceAll("^[A-Z0-9\\-\\/]{2,}\\s+", " ");
        valor = valor.replaceAll("\\s{2,}", " ").trim();

        if (valor.length() < 3) {
            return null;
        }

        return valor;
    }

    private boolean semblaLiniaDeProducte(String linia) {
        String valor = normalitzarPerComparar(linia);

        if (!valor.matches(".*[A-ZÀ-ÿ].*")) {
            return false;
        }

        if (valor.contains("TOTAL") || valor.contains("BASE IMP") || valor.contains("IVA")
                || valor.contains("FORMA DE PAGAMENT") || valor.contains("SIGNATURA")
                || valor.contains("OBSERVACIONES") || valor.contains("OBSERVACIONS")
                || valor.contains("CLIENT") || valor.contains("NIF")
                || valor.contains("ALBARAN") || valor.contains("ALBARA")
                || valor.contains("FECHA") || valor.contains("DATA")
                || valor.contains("COPIA")) {
            return false;
        }

        return valor.length() >= 5;
    }

    private String extreurePrimer(String text, Pattern patro) {
        if (text == null) {
            return null;
        }

        Matcher matcher = patro.matcher(text);

        if (matcher.find()) {
            return netejarValor(matcher.group(1));
        }

        return null;
    }

    private boolean esLotValid(String lot) {
        if (lot == null || lot.isBlank()) {
            return false;
        }

        if (PATRON_DATA.matcher(lot).find()) {
            return false;
        }

        if (PATRON_CIF_NIF.matcher(lot).find()) {
            return false;
        }

        if (lot.matches("\\d{1,4}")) {
            return false;
        }

        return lot.matches(".*\\d.*") && lot.length() >= 4;
    }

    private String normalitzarLot(String lot) {
        if (lot == null) {
            return null;
        }

        String valor = lot.toUpperCase(Locale.ROOT)
                .replace(" ", "-")
                .replace(".", "-")
                .replace("/", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^[^A-Z0-9]+", "")
                .replaceAll("[^A-Z0-9]+$", "")
                .trim();

        String[] parts = valor.split("-");

        if (parts.length >= 2 && parts[1].matches("\\d{1,2}") && parts[0].length() >= 4) {
            return parts[0];
        }

        return valor;
    }

    private List<String> obtenirLinies(String textOcr) {
        List<String> linies = new ArrayList<>();

        if (textOcr == null || textOcr.isBlank()) {
            return linies;
        }

        String[] parts = textOcr.split("\\R");

        for (String part : parts) {
            String linia = netejarValor(part);

            if (linia != null && !linia.isBlank()) {
                linies.add(linia);
            }
        }

        return linies;
    }

    private String normalitzarText(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("\r", "\n")
                .replace("\t", " ")
                .replace("º", "o")
                .replace("ª", "a")
                .replaceAll("[ ]{2,}", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private String normalitzarPerComparar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.toUpperCase(Locale.ROOT)
                .replace("Á", "A")
                .replace("À", "A")
                .replace("É", "E")
                .replace("È", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ò", "O")
                .replace("Ú", "U")
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
        if (valor == null) {
            return null;
        }

        return valor
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ")
                .trim()
                .replaceAll("\\s{2,}", " ");
    }
}
