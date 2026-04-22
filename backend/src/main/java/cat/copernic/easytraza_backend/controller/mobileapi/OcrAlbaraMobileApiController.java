package cat.copernic.easytraza_backend.controller.mobileapi;

import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.service.OcrAlbaraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mobile-api/ocr/albarans-proveidor")
public class OcrAlbaraMobileApiController {

    @Autowired
    private OcrAlbaraService ocrAlbaraService;

    @PostMapping(
            value = "/analitzar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OcrAlbaraResponseDto analitzarAlbara(@RequestParam("imatge") MultipartFile imatge) {
        return ocrAlbaraService.processarImatgeAlbara(imatge);
    }
}
