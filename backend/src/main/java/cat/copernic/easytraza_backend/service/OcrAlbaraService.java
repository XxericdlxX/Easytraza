package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.OcrLotRespostaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class OcrAlbaraService {

    @Value("${ocr.space.api.url}")
    private String ocrApiUrl;

    @Value("${ocr.space.api.key}")
    private String ocrApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public OcrAlbaraResponseDto processarImatgeAlbara(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("La imatge és obligatòria.");
        }

        String textOcr = cridarOcrSpace(image);
        return parsejarTextOcr(textOcr);
    }

    private String cridarOcrSpace(MultipartFile image) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("apikey", ocrApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("language", "spa");
            body.add("isOverlayRequired", "false");
            body.add("OCREngine", "2");
            body.add("file", buildFileResource(image));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    ocrApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("No s'ha pogut obtenir resposta de l'OCR.");
            }

            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode parsedResults = root.path("ParsedResults");
            if (!parsedResults.isArray() || parsedResults.isEmpty()) {
                throw new IllegalStateException("L'OCR no ha retornat text processat.");
            }

            StringBuilder textFinal = new StringBuilder();
            for (JsonNode result : parsedResults) {
                String parsedText = result.path("ParsedText").asText("");
                if (!parsedText.isBlank()) {
                    textFinal.append(parsedText).append("\n");
                }
            }

            if (textFinal.toString().isBlank()) {
                throw new IllegalStateException("L'OCR no ha detectat text a la imatge.");
            }

            return textFinal.toString().trim();

        } catch (IOException ex) {
            throw new IllegalStateException("Error processant la resposta de l'OCR.", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Error cridant el servei OCR.", ex);
        }
    }

    private ByteArrayResource buildFileResource(MultipartFile image) throws IOException {
        return new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename() != null
                        ? image.getOriginalFilename()
                        : "albara.jpg";
            }
        };
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
