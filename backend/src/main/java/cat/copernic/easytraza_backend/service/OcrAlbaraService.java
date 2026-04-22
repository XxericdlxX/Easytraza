package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.OcrLotRespostaDto;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrAlbaraService {

    @Value("${ocr.tessdata.path}")
    private String tessdataPath;

    @Value("${ocr.tesseract.language}")
    private String tesseractLanguage;

    public OcrAlbaraResponseDto processarImatgeAlbara(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("La imatge és obligatòria.");
        }

        String textOcr = extreureText(image);
        return parsejarTextOcr(textOcr);
    }

    private String extreureText(MultipartFile image) {
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

            if (bufferedImage == null) {
                throw new IllegalStateException("No s'ha pogut llegir la imatge enviada.");
            }

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessdataPath);
            tesseract.setLanguage(tesseractLanguage);

            return tesseract.doOCR(bufferedImage);

        } catch (IOException ex) {
            throw new IllegalStateException("Error llegint la imatge per OCR.", ex);
        } catch (TesseractException ex) {
            throw new IllegalStateException("Error executant Tesseract OCR.", ex);
        }
    }

    private OcrAlbaraResponseDto parsejarTextOcr(String textOcr) {
        OcrAlbaraResponseDto resposta = new OcrAlbaraResponseDto();
        resposta.setTextDetectat(textOcr);

        resposta.setProveidorCif(extreurePrimerCoincident(
                textOcr,
                "(?i)\\b([ABCDEFGHJNPQRSUVW]\\d{7}[0-9A-J]|\\d{8}[A-Z]|[XYZ]\\d{7}[A-Z])\\b"
        ));

        resposta.setNumeroAlbara(extreurePrimerCoincident(
                textOcr,
                "(?i)(?:albar[aá]n|num(?:ero)?\\s*albar[aá]n|n[uú]m(?:ero)?)[:\\s#-]*([A-Z0-9\\-/]+)"
        ));

        resposta.setDataAlbara(extreurePrimerCoincident(
                textOcr,
                "\\b(\\d{2}[/-]\\d{2}[/-]\\d{4}|\\d{4}[/-]\\d{2}[/-]\\d{2})\\b"
        ));

        OcrLotRespostaDto lot = new OcrLotRespostaDto();
        lot.setCodiLot(extreurePrimerCoincident(
                textOcr,
                "(?i)(?:lot|lote|codi\\s*lot|codigo\\s*de\\s*lote)[:\\s#-]*([A-Z0-9\\-/]+)"
        ));
        lot.setQuantitat(extreurePrimeraQuantitat(textOcr));
        lot.setMateriaPrima(extreureMateriaPrima(textOcr));

        if (lot.getCodiLot() != null || lot.getQuantitat() != null || lot.getMateriaPrima() != null) {
            resposta.getLots().add(lot);
        }

        return resposta;
    }

    private String extreurePrimerCoincident(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return netejarValor(matcher.group(1));
        }
        return null;
    }

    private Double extreurePrimeraQuantitat(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:quantitat|cantidad)[:\\s]*([0-9]+(?:[\\.,][0-9]+)?)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String valor = matcher.group(1).replace(",", ".");
            try {
                return Double.valueOf(valor);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String extreureMateriaPrima(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:mat[eè]ria\\s*prima|materia\\s*prima)[:\\s]*([A-ZÀ-ÿa-z0-9\\s\\-]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return netejarValor(matcher.group(1));
        }
        return null;
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
