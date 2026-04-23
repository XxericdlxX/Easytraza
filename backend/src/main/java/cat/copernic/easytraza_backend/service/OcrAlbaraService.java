package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.OcrLotRespostaDto;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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

    private static final Pattern PATTERN_CIF = Pattern.compile(
            "\\b([A-Z]\\d{7,8}|\\d{8}[A-Z]|[XYZ]\\d{7}[A-Z])\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATTERN_DATA = Pattern.compile(
            "\\b(\\d{2}[/-]\\d{2}[/-]\\d{4}|\\d{4}[/-]\\d{2}[/-]\\d{2})\\b"
    );

    private static final Pattern PATTERN_NUMERO = Pattern.compile(
            "\\b(ALB\\s*[- ]?\\d+|ALBARA\\s*[- ]?\\d+|ALBARAN\\s*[- ]?\\d+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATTERN_LOT = Pattern.compile(
            "\\b((?:LOT|L0T|10T|LOTE|L)[-\\s]?[A-Z0-9][A-Z0-9\\-]{1,})\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATTERN_QUANTITAT = Pattern.compile(
            "\\b(\\d+(?:[\\.,]\\d+)?)\\s*(KG|KGS|G|GR|L|ML|UD|UDS|UNITATS)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public OcrAlbaraResponseDto processarImatgeAlbara(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("El document és obligatori.");
        }

        String originalName = Optional.ofNullable(image.getOriginalFilename())
                .orElse("")
                .toLowerCase(Locale.ROOT);

        String textOcr;
        if (originalName.endsWith(".pdf")) {
            textOcr = extreureTextPdf(image);
        } else {
            textOcr = extreureTextImatge(image);
        }

        return parsejarTextOcr(normalitzarText(textOcr));
    }

    private String extreureTextImatge(MultipartFile image) {
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

            if (bufferedImage == null) {
                throw new IllegalStateException("No s'ha pogut llegir la imatge enviada.");
            }

            Tesseract tesseract = buildTesseract();
            return tesseract.doOCR(bufferedImage);

        } catch (IOException ex) {
            throw new IllegalStateException("Error llegint la imatge per OCR.", ex);
        } catch (TesseractException ex) {
            throw new IllegalStateException("Error executant Tesseract OCR.", ex);
        }
    }

    private String extreureTextPdf(MultipartFile pdfFile) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("easytraza_ocr_", ".pdf");
            pdfFile.transferTo(tempFile);

            StringBuilder resultat = new StringBuilder();
            Tesseract tesseract = buildTesseract();

            try (PDDocument document = Loader.loadPDF(tempFile)) {
                PDFRenderer renderer = new PDFRenderer(document);

                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    BufferedImage image = renderer.renderImageWithDPI(i, 250, ImageType.RGB);
                    resultat.append(tesseract.doOCR(image)).append("\n");
                }
            }

            return resultat.toString();

        } catch (Exception ex) {
            throw new IllegalStateException("Error processant el PDF per OCR.", ex);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private Tesseract buildTesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(tesseractLanguage);
        return tesseract;
    }

    private OcrAlbaraResponseDto parsejarTextOcr(String textOcr) {
        OcrAlbaraResponseDto resposta = new OcrAlbaraResponseDto();
        resposta.setTextDetectat(textOcr);

        resposta.setProveidorCif(extraurePrimerCoincident(textOcr, PATTERN_CIF));
        resposta.setNumeroAlbara(extraurePrimerCoincident(textOcr, PATTERN_NUMERO));
        resposta.setDataAlbara(extraurePrimerCoincident(textOcr, PATTERN_DATA));

        List<OcrLotRespostaDto> lots = extreureLots(textOcr);
        if (lots.isEmpty()) {
            OcrLotRespostaDto fallback = construirLotFallback(textOcr);
            if (fallback != null) {
                lots.add(fallback);
            }
        }

        resposta.setLots(lots);
        return resposta;
    }

    private List<OcrLotRespostaDto> extreureLots(String textOcr) {
        List<String> lines = textOcr.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .toList();

        List<OcrLotRespostaDto> lots = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);
            String lot = extraureLot(currentLine);

            if (lot == null) {
                continue;
            }

            String materia = cercarMateriaProp(lines, i);
            Double quantitat = cercarQuantitatProp(lines, i);

            OcrLotRespostaDto dto = new OcrLotRespostaDto();
            dto.setCodiLot(netejarValor(lot));
            dto.setMateriaPrima(materia);
            dto.setQuantitat(quantitat);

            lots.add(dto);
        }

        return fusionarLotsDuplicats(lots);
    }

    private OcrLotRespostaDto construirLotFallback(String textOcr) {
        List<String> lines = textOcr.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .toList();

        String lot = null;
        String materia = null;
        Double quantitat = null;

        for (String line : lines) {
            if (lot == null) {
                lot = extraureLot(line);
            }
            if (materia == null && esPossibleMateria(line)) {
                materia = line;
            }
            if (quantitat == null) {
                quantitat = extraureQuantitat(line);
            }
        }

        if (lot == null && materia == null && quantitat == null) {
            return null;
        }

        OcrLotRespostaDto dto = new OcrLotRespostaDto();
        dto.setCodiLot(netejarValor(lot));
        dto.setMateriaPrima(netejarValor(materia));
        dto.setQuantitat(quantitat);
        return dto;
    }

    private List<OcrLotRespostaDto> fusionarLotsDuplicats(List<OcrLotRespostaDto> input) {
        List<OcrLotRespostaDto> resultat = new ArrayList<>();

        for (OcrLotRespostaDto actual : input) {
            if (actual.getCodiLot() == null || actual.getCodiLot().isBlank()) {
                resultat.add(actual);
                continue;
            }

            Optional<OcrLotRespostaDto> existent = resultat.stream()
                    .filter(l -> actual.getCodiLot().equalsIgnoreCase(l.getCodiLot()))
                    .findFirst();

            if (existent.isPresent()) {
                OcrLotRespostaDto lot = existent.get();

                if ((lot.getMateriaPrima() == null || lot.getMateriaPrima().isBlank())
                        && actual.getMateriaPrima() != null
                        && !actual.getMateriaPrima().isBlank()) {
                    lot.setMateriaPrima(actual.getMateriaPrima());
                }

                if (lot.getQuantitat() == null && actual.getQuantitat() != null) {
                    lot.setQuantitat(actual.getQuantitat());
                }
            } else {
                resultat.add(actual);
            }
        }

        resultat.sort(Comparator.comparing(l -> Optional.ofNullable(l.getCodiLot()).orElse("ZZZ")));
        return resultat;
    }

    private String cercarMateriaProp(List<String> lines, int lotIndex) {
        List<String> candidates = new ArrayList<>();

        if (lotIndex - 2 >= 0) {
            candidates.add(lines.get(lotIndex - 2));
        }
        if (lotIndex - 1 >= 0) {
            candidates.add(lines.get(lotIndex - 1));
        }
        if (lotIndex + 1 < lines.size()) {
            candidates.add(lines.get(lotIndex + 1));
        }
        if (lotIndex + 2 < lines.size()) {
            candidates.add(lines.get(lotIndex + 2));
        }

        return candidates.stream()
                .filter(this::esPossibleMateria)
                .map(this::netejarValor)
                .findFirst()
                .orElse(null);
    }

    private Double cercarQuantitatProp(List<String> lines, int lotIndex) {
        List<String> candidates = new ArrayList<>();

        if (lotIndex + 1 < lines.size()) {
            candidates.add(lines.get(lotIndex + 1));
        }
        if (lotIndex + 2 < lines.size()) {
            candidates.add(lines.get(lotIndex + 2));
        }
        if (lotIndex - 1 >= 0) {
            candidates.add(lines.get(lotIndex - 1));
        }

        for (String line : candidates) {
            Double q = extraureQuantitat(line);
            if (q != null) {
                return q;
            }
        }

        return null;
    }

    private String extraurePrimerCoincident(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return netejarValor(matcher.group(1));
        }
        return null;
    }

    private String extraureLot(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String normalitzada = line.toUpperCase(Locale.ROOT)
                .replace("L0T", "LOT")
                .replace("10T", "LOT")
                .replace("IOT", "LOT")
                .replace("LO7", "LOT");

        Matcher matcher = PATTERN_LOT.matcher(normalitzada);
        if (matcher.find()) {
            String lot = matcher.group(1);
            lot = lot.replaceAll("\\s+", "-");
            lot = lot.replaceAll("-{2,}", "-");
            return lot;
        }

        return null;
    }

    private Double extraureQuantitat(String line) {
        Matcher matcher = PATTERN_QUANTITAT.matcher(line.toUpperCase(Locale.ROOT));
        while (matcher.find()) {
            String numeric = matcher.group(1);
            if (numeric != null) {
                try {
                    return Double.valueOf(numeric.replace(",", "."));
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private boolean esPossibleMateria(String line) {
        String value = line.trim();

        if (value.isBlank()) {
            return false;
        }
        if (PATTERN_CIF.matcher(value).find()) {
            return false;
        }
        if (PATTERN_DATA.matcher(value).find()) {
            return false;
        }
        if (PATTERN_NUMERO.matcher(value).find()) {
            return false;
        }
        if (PATTERN_LOT.matcher(value).find()) {
            return false;
        }
        if (PATTERN_QUANTITAT.matcher(value).find()) {
            return false;
        }
        if (!value.matches(".*[A-Za-zÀ-ÿ].*")) {
            return false;
        }

        return value.length() >= 3;
    }

    private String normalitzarText(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("\r", "\n")
                .replaceAll("[\\t]+", " ")
                .replaceAll("[ ]{2,}", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private String netejarValor(String valor) {
        if (valor == null) {
            return null;
        }

        return valor
                .replace("\n", " ")
                .replace("\r", " ")
                .trim()
                .replaceAll("\\s{2,}", " ");
    }
}
