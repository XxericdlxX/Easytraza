package cat.copernic.easytraza_backend.controller.mobileapi;

import cat.copernic.easytraza_backend.dto.mobile.MobileLotDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.service.LotProveidorService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de l’API mobile `MobileLotsApiController` del projecte EasyTraza.
 */
@RestController
@RequestMapping("/mobile-api/lots")
public class MobileLotsApiController {

    @Autowired
    private LotProveidorService lotProveidorService;

    /**
     * Executa l'operació `llistarLots`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping
    public List<MobileLotDto> llistarLots() {
        return lotProveidorService.findAll().stream()
                .sorted(Comparator
                        .comparing(this::obtenirDataRecepcioSegura, Comparator.reverseOrder())
                        .thenComparing(LotProveidor::getId, Comparator.reverseOrder()))
                .map(this::toDto)
                .toList();
    }

    /**
     * Executa l'operació `iniciarLot`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<MobileLotDto> iniciarLot(@PathVariable Long id) {
        LotProveidor lot = lotProveidorService.iniciarLot(id);
        return ResponseEntity.ok(toDto(lot));
    }

    /**
     * Executa l'operació `finalitzarLot`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/{id}/finalitzar")
    public ResponseEntity<MobileLotDto> finalitzarLot(@PathVariable Long id) {
        LotProveidor lot = lotProveidorService.finalitzarLot(id);
        return ResponseEntity.ok(toDto(lot));
    }

    /**
     * Executa l'operació `toDto`.
     *
     * @param lot paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private MobileLotDto toDto(LotProveidor lot) {
        MobileLotDto dto = new MobileLotDto();

        dto.setId(lot.getId());
        dto.setCodiLot(lot.getCodiLot());
        dto.setQuantitat(lot.getQuantitat());
        dto.setEstat(lot.getEstat() != null ? lot.getEstat().name() : null);

        MateriaPrima materiaPrima = lot.getMateriaPrima();
        if (materiaPrima != null) {
            dto.setMateriaPrimaNom(materiaPrima.getNom());
        }

        Proveidor proveidor = lot.getProveidor();
        if (proveidor != null) {
            dto.setProveidorNom(proveidor.getNom());
            dto.setProveidorCif(proveidor.getCif());
        }

        AlbaraProveidor albara = lot.getAlbaraProveidor();
        if (albara != null) {
            dto.setAlbaraProveidorId(albara.getId());

            if (albara.getDataRecepcio() != null) {
                dto.setDataRecepcio(albara.getDataRecepcio().toString());
            }
        }

        if (lot.getDataObertura() != null) {
            dto.setDataObertura(lot.getDataObertura().toString());
        }

        if (lot.getDataAcabament() != null) {
            dto.setDataAcabament(lot.getDataAcabament().toString());
        }

        return dto;
    }

    /**
     * Executa l'operació `obtenirDataRecepcioSegura`.
     *
     * @param lot paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private LocalDate obtenirDataRecepcioSegura(LotProveidor lot) {
        if (lot.getAlbaraProveidor() != null && lot.getAlbaraProveidor().getDataRecepcio() != null) {
            return lot.getAlbaraProveidor().getDataRecepcio();
        }

        return LocalDate.MIN;
    }
}
