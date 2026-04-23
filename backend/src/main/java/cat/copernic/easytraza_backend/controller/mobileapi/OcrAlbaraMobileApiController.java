package cat.copernic.easytraza_backend.controller.mobileapi;

import cat.copernic.easytraza_backend.dto.AlbaraProveidorDto;
import cat.copernic.easytraza_backend.dto.LotProveidorDto;
import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.mobile.MobileAlbaraSaveRequestDto;
import cat.copernic.easytraza_backend.dto.mobile.MobileLotSaveRequestDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import cat.copernic.easytraza_backend.service.AlbaraProveidorService;
import cat.copernic.easytraza_backend.service.OcrAlbaraService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mobile-api/ocr/albarans-proveidor")
public class OcrAlbaraMobileApiController {

    @Autowired
    private OcrAlbaraService ocrAlbaraService;

    @Autowired
    private AlbaraProveidorService albaraProveidorService;

    @Autowired
    private ProveidorRepository proveidorRepository;

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    @PostMapping(
            value = "/analitzar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OcrAlbaraResponseDto analitzarAlbara(@RequestParam("fitxer") MultipartFile fitxer) {
        return ocrAlbaraService.processarImatgeAlbara(fitxer);
    }

    @PostMapping(
            value = "/guardar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> guardarAlbara(@RequestBody MobileAlbaraSaveRequestDto request) {
        try {
            AlbaraProveidorDto dto = new AlbaraProveidorDto();
            dto.setDataRecepcio(parseData(request.getDataRecepcio()));
            dto.setProveidorCif(resoldreOCrearProveidor(request.getProveidorCif(), request.getProveidorNom()));
            dto.setLots(convertirLots(request.getLots()));

            String errorNegoci = albaraProveidorService.validarAlbara(dto, null);
            if (errorNegoci != null) {
                return ResponseEntity.badRequest().body(errorNegoci);
            }

            AlbaraProveidor entity = albaraProveidorService.convertirDtoAEntity(dto);
            albaraProveidorService.save(entity);

            return ResponseEntity.ok().build();

        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("No s'ha pogut desar l'albarà des del client mobile.");
        }
    }

    private LocalDate parseData(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.now();
        }

        String clean = value.trim().replace("/", "-");
        String[] parts = clean.split("-");

        if (parts.length == 3 && parts[0].length() == 2) {
            clean = parts[2] + "-" + parts[1] + "-" + parts[0];
        }

        return LocalDate.parse(clean);
    }

    private List<LotProveidorDto> convertirLots(List<MobileLotSaveRequestDto> lotsRequest) {
        List<LotProveidorDto> lots = new ArrayList<>();

        if (lotsRequest != null) {
            for (MobileLotSaveRequestDto lotRequest : lotsRequest) {
                if (lotRequest == null) {
                    continue;
                }

                String codiLot = lotRequest.getCodiLot() != null ? lotRequest.getCodiLot().trim() : "";
                String materiaNom = lotRequest.getMateriaPrimaNom() != null ? lotRequest.getMateriaPrimaNom().trim() : "";

                if (codiLot.isBlank() && materiaNom.isBlank() && lotRequest.getQuantitat() == null) {
                    continue;
                }

                LotProveidorDto lot = new LotProveidorDto();
                lot.setCodiLot(codiLot);
                lot.setQuantitat(lotRequest.getQuantitat());
                lot.setMateriaPrimaId(resoldreOCrearMateriaPrima(materiaNom));
                lots.add(lot);
            }
        }

        return lots;
    }

    private String resoldreOCrearProveidor(String proveidorCif, String proveidorNom) {
        String cif = normalitzar(proveidorCif);
        String nom = netejar(proveidorNom);

        if (cif != null && !cif.isBlank()) {
            Optional<Proveidor> existent = proveidorRepository.findById(cif);
            if (existent.isPresent()) {
                return existent.get().getCif();
            }

            Proveidor nou = new Proveidor();
            nou.setCif(cif);
            nou.setNom((nom != null && !nom.isBlank()) ? nom : "Proveïdor OCR " + cif);
            nou.setAdreca("Pendent OCR");
            nou.setNotes("Creat automàticament des del client mobile / OCR");
            nou.setTelefon(null);
            nou.setEmail(null);

            proveidorRepository.save(nou);
            return nou.getCif();
        }

        if (nom != null && !nom.isBlank()) {
            Optional<Proveidor> existentNom = proveidorRepository.findByNomIgnoreCase(nom);
            if (existentNom.isPresent()) {
                return existentNom.get().getCif();
            }
        }

        return null;
    }

    private Long resoldreOCrearMateriaPrima(String materiaPrimaNom) {
        String nom = netejar(materiaPrimaNom);
        if (nom == null || nom.isBlank()) {
            return null;
        }

        Optional<MateriaPrima> existent = materiaPrimaRepository.findByNomIgnoreCase(nom);
        if (existent.isPresent()) {
            return existent.get().getId();
        }

        MateriaPrima nova = new MateriaPrima();
        nova.setNom(nom);
        nova.setDescripcio("Creada automàticament des del client mobile / OCR");

        materiaPrimaRepository.save(nova);
        return nova.getId();
    }

    private String normalitzar(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase().replace(" ", "");
    }

    private String netejar(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().replaceAll("\\s{2,}", " ");
    }
}
