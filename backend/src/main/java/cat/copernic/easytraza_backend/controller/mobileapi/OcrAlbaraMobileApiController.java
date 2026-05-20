package cat.copernic.easytraza_backend.controller.mobileapi;

import cat.copernic.easytraza_backend.dto.AlbaraProveidorDto;
import cat.copernic.easytraza_backend.dto.LotProveidorDto;
import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.mobile.MobileAlbaraSaveRequestDto;
import cat.copernic.easytraza_backend.dto.mobile.MobileLotSaveRequestDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
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

/**
 * Controlador de l’API mobile `OcrAlbaraMobileApiController` del projecte
 * EasyTraza.
 */
@RestController
@RequestMapping("/mobile-api/ocr/albarans-proveidor")
public class OcrAlbaraMobileApiController {

    @Autowired
    private OcrAlbaraService ocrAlbaraService;

    @Autowired
    private AlbaraProveidorService albaraProveidorService;

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @PostMapping(
            value = "/analitzar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    /**
     * Executa l'operació `analitzarAlbara`.
     *
     * @param fitxer paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public OcrAlbaraResponseDto analitzarAlbara(@RequestParam("fitxer") MultipartFile fitxer) {
        return ocrAlbaraService.processarImatgeAlbara(fitxer);
    }

    @PostMapping(
            value = "/guardar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    /**
     * Executa l'operació `guardarAlbara`.
     *
     * @param request paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public ResponseEntity<?> guardarAlbara(@RequestBody MobileAlbaraSaveRequestDto request) {
        try {
            AlbaraProveidorDto dto = new AlbaraProveidorDto();

            dto.setDataRecepcio(parseData(request.getDataRecepcio()));
            dto.setProveidorCif(normalitzarDocument(request.getProveidorCif()));
            dto.setProveidorNomDetectat(netejarNomProveidor(request.getProveidorNom()));
            dto.setCrearProveidorSiNoExisteix(request.isCrearProveidorSiNoExisteix());
            dto.setLots(convertirLots(request.getLots()));

            String errorNegoci = albaraProveidorService.validarAlbara(dto, null);
            if (errorNegoci != null) {
                return ResponseEntity.badRequest().body(errorNegoci);
            }

            Optional<Usuari> usuariReceptor = obtenirUsuariReceptor(request.getUsuariReceptorId());
            if (usuariReceptor.isEmpty()) {
                return ResponseEntity.badRequest().body("mobile.usuari.no.trobat");
            }

            AlbaraProveidor entity = albaraProveidorService.convertirDtoAEntity(dto);
            entity.setUsuariReceptor(usuariReceptor.get());
            albaraProveidorService.save(entity);

            return ResponseEntity.ok().build();

        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("No s'ha pogut desar l'albarà des del client mobile.");
        }
    }

    /**
     * Executa l'operació `obtenirUsuariReceptor`.
     *
     * @param usuariReceptorId paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private Optional<Usuari> obtenirUsuariReceptor(Long usuariReceptorId) {
        if (usuariReceptorId == null) {
            return Optional.empty();
        }

        return usuariRepository.findById(usuariReceptorId);
    }

    /**
     * Executa l'operació `parseData`.
     *
     * @param value paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `convertirLots`.
     *
     * @param lotsRequest paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private List<LotProveidorDto> convertirLots(List<MobileLotSaveRequestDto> lotsRequest) {
        List<LotProveidorDto> lots = new ArrayList<>();

        if (lotsRequest != null) {
            for (MobileLotSaveRequestDto lotRequest : lotsRequest) {
                if (lotRequest == null) {
                    continue;
                }

                String codiLot = netejar(lotRequest.getCodiLot());
                String codiMateriaPrimaOcr = netejar(lotRequest.getCodiMateriaPrimaOcr());
                String materiaNom = netejar(lotRequest.getMateriaPrimaNom());

                if ((codiLot == null || codiLot.isBlank())
                        && (materiaNom == null || materiaNom.isBlank())
                        && lotRequest.getQuantitat() == null) {
                    continue;
                }

                LotProveidorDto lot = new LotProveidorDto();
                lot.setCodiLot(codiLot);
                lot.setCodiMateriaPrimaOcr(codiMateriaPrimaOcr);
                lot.setQuantitat(lotRequest.getQuantitat());
                lot.setCrearMateriaPrimaSiNoExisteix(lotRequest.isCrearMateriaPrimaSiNoExisteix());

                Optional<MateriaPrima> materiaExistent = buscarMateriaPrima(materiaNom);

                if (materiaExistent.isPresent()) {
                    lot.setMateriaPrimaId(materiaExistent.get().getId());
                    lot.setMateriaPrimaNomDetectada(materiaExistent.get().getNom());
                } else {
                    lot.setMateriaPrimaNomDetectada(materiaNom);
                }

                lots.add(lot);
            }
        }

        return lots;
    }

    /**
     * Executa l'operació `buscarMateriaPrima`.
     *
     * @param materiaNom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private Optional<MateriaPrima> buscarMateriaPrima(String materiaNom) {
        if (materiaNom == null || materiaNom.isBlank()) {
            return Optional.empty();
        }

        return materiaPrimaRepository.findByNomIgnoreCase(materiaNom);
    }

    /**
     * Executa l'operació `normalitzarDocument`.
     *
     * @param value paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarDocument(String value) {
        if (value == null) {
            return null;
        }

        String normalitzat = value.trim().toUpperCase().replace(" ", "");
        return normalitzat.isBlank() ? null : normalitzat;
    }

    /**
     * Executa l'operació `netejarNomProveidor`.
     *
     * @param value paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String netejarNomProveidor(String value) {
        String net = netejar(value);

        if (net == null || net.isBlank()) {
            return null;
        }

        net = net.replaceFirst("(?i)^PROVEIDOR\\s+DETECTAT\\s+OCR\\s*:\\s*", "");
        net = net.replaceFirst("(?i)^PROVEEDOR\\s+DETECTADO\\s+POR\\s+OCR\\s*:\\s*", "");
        net = net.replaceFirst("(?i)^PROVEÏDOR\\s+DETECTAT\\s+PER\\s+OCR\\s*:\\s*", "");
        net = net.replaceAll("\\s{2,}", " ").trim();

        return net.isBlank() ? null : net;
    }

    /**
     * Executa l'operació `netejar`.
     *
     * @param value paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String netejar(String value) {
        if (value == null) {
            return null;
        }

        String net = value.trim().replaceAll("\\s{2,}", " ");
        return net.isBlank() ? null : net;
    }
}
