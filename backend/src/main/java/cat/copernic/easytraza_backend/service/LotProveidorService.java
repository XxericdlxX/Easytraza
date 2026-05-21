package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.model.LiniaClient;
import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import cat.copernic.easytraza_backend.repository.AlbaraClientRepository;
import cat.copernic.easytraza_backend.repository.LotProveidorRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei `LotProveidorService` del projecte EasyTraza.
 */
@Service
public class LotProveidorService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.lots");

    @Autowired
    private LotProveidorRepository lotProveidorRepository;

    @Autowired
    private AlbaraClientRepository albaraClientRepository;

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LotProveidor> findAll() {
        return lotProveidorRepository.findAll();
    }

    /**
     * Executa l'operació `findById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Optional<LotProveidor> findById(Long id) {
        return lotProveidorRepository.findById(id);
    }

    /**
     * Executa l'operació `findProduccioAssociadaAlLot`.
     *
     * @param lotId paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LiniaClient> findProduccioAssociadaAlLot(Long lotId) {
        return albaraClientRepository.findLiniesProduccioByLotId(lotId);
    }

    /**
     * Executa l'operació `existeixLotObert`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean existeixLotObert() {
        return !lotProveidorRepository.findByEstat(EstatLot.OBERT).isEmpty();
    }

    /**
     * Executa l'operació `cercar`.
     *
     * @param codiLot paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @param materiaPrimaId paràmetre necessari per a l'operació.
     * @param dataRecepcio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LotProveidor> cercar(String codiLot, EstatLot estat, Long materiaPrimaId, LocalDate dataRecepcio) {
        return cercar(codiLot, estat, materiaPrimaId, dataRecepcio, "dataRecepcio", "desc");
    }

    /**
     * Executa l'operació `cercar`.
     *
     * @param codiLot paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @param materiaPrimaId paràmetre necessari per a l'operació.
     * @param dataRecepcio paràmetre necessari per a l'operació.
     * @param sortField paràmetre necessari per a l'operació.
     * @param sortDir paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LotProveidor> cercar(String codiLot,
            EstatLot estat,
            Long materiaPrimaId,
            LocalDate dataRecepcio,
            String sortField,
            String sortDir) {

        List<LotProveidor> lots = lotProveidorRepository.cercarLots(
                normalitzarTextCerca(codiLot),
                estat,
                materiaPrimaId,
                dataRecepcio
        );

        return ordenarLots(lots, sortField, sortDir);
    }

    /**
     * Executa l'operació `iniciarLot`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Transactional
    public LotProveidor iniciarLot(Long id) {
        LotProveidor lot = lotProveidorRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("No s'ha pogut iniciar el lot perquè no existeix. Id: {}", id);
                    return new IllegalArgumentException("lots.error.no.trobat");
                });

        if (lot.getEstat() == EstatLot.ACABAT) {
            LOGGER.warn("Intent d'iniciar un lot acabat. Id: {}", id);
            throw new IllegalStateException("lots.error.iniciar.acabat");
        }

        if (lot.getEstat() == EstatLot.OBERT) {
            LOGGER.warn("Intent d'iniciar un lot que ja estava obert. Id: {}", id);
            throw new IllegalStateException("lots.error.iniciar.obert");
        }

        if (lot.getMateriaPrima() == null || lot.getMateriaPrima().getId() == null) {
            LOGGER.warn("No s'ha pogut iniciar el lot perquè no té matèria primera associada. Id: {}", id);
            throw new IllegalStateException("lots.error.materia.no.trobada");
        }

        List<LotProveidor> lotsObertsMateixaMateria
                = lotProveidorRepository.findByMateriaPrima_IdAndEstat(
                        lot.getMateriaPrima().getId(),
                        EstatLot.OBERT
                );

        LocalDate avui = LocalDate.now();

        for (LotProveidor lotObert : lotsObertsMateixaMateria) {
            if (!lotObert.getId().equals(lot.getId())) {
                lotObert.setEstat(EstatLot.ACABAT);
                lotObert.setDataAcabament(avui);
                lotProveidorRepository.save(lotObert);
                LOGGER.info("Lot obert anterior finalitzat automàticament. Id: {}", lotObert.getId());
            }
        }

        lot.setEstat(EstatLot.OBERT);
        lot.setDataObertura(avui);
        lot.setDataAcabament(null);

        try {
            LotProveidor lotDesat = lotProveidorRepository.save(lot);
            LOGGER.info("Lot iniciat correctament. Id: {}", lotDesat.getId());
            return lotDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en iniciar el lot amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `finalitzarLot`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Transactional
    public LotProveidor finalitzarLot(Long id) {
        LotProveidor lot = lotProveidorRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("No s'ha pogut finalitzar el lot perquè no existeix. Id: {}", id);
                    return new IllegalArgumentException("lots.error.no.trobat");
                });

        if (lot.getEstat() == EstatLot.EN_ESTOC) {
            LOGGER.warn("Intent de finalitzar un lot que encara està en estoc. Id: {}", id);
            throw new IllegalStateException("lots.error.finalitzar.estoc");
        }

        if (lot.getEstat() == EstatLot.ACABAT) {
            LOGGER.warn("Intent de finalitzar un lot que ja estava acabat. Id: {}", id);
            throw new IllegalStateException("lots.error.finalitzar.acabat");
        }

        lot.setEstat(EstatLot.ACABAT);
        lot.setDataAcabament(LocalDate.now());

        try {
            LotProveidor lotDesat = lotProveidorRepository.save(lot);
            LOGGER.info("Lot finalitzat correctament. Id: {}", lotDesat.getId());
            return lotDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en finalitzar el lot amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `ordenarLots`.
     *
     * @param lots paràmetre necessari per a l'operació.
     * @param sortField paràmetre necessari per a l'operació.
     * @param sortDir paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private List<LotProveidor> ordenarLots(List<LotProveidor> lots, String sortField, String sortDir) {
        Comparator<LotProveidor> comparador = obtenirComparador(sortField);

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparador = comparador.reversed();
        }

        return lots.stream()
                .sorted(comparador)
                .toList();
    }

    /**
     * Executa l'operació `obtenirComparador`.
     *
     * @param sortField paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private Comparator<LotProveidor> obtenirComparador(String sortField) {
        String camp = sortField == null || sortField.isBlank() ? "dataRecepcio" : sortField;

        return switch (camp) {
            case "codiLot" ->
                Comparator.comparing(lot -> text(lot.getCodiLot()), String.CASE_INSENSITIVE_ORDER);

            case "materiaPrima" ->
                Comparator.comparing(
                lot -> lot.getMateriaPrima() != null ? text(lot.getMateriaPrima().getNom()) : "",
                String.CASE_INSENSITIVE_ORDER
                );

            case "proveidor" ->
                Comparator.comparing(
                lot -> lot.getProveidor() != null ? text(lot.getProveidor().getNom()) : "",
                String.CASE_INSENSITIVE_ORDER
                );

            case "quantitat" ->
                Comparator.comparing(lot -> lot.getQuantitat() != null ? lot.getQuantitat() : 0.0);

            case "dataObertura" ->
                Comparator.comparing(lot -> lot.getDataObertura() != null ? lot.getDataObertura() : LocalDate.MIN);

            case "dataAcabament" ->
                Comparator.comparing(lot -> lot.getDataAcabament() != null ? lot.getDataAcabament() : LocalDate.MIN);

            case "estat" ->
                Comparator.comparing(
                lot -> lot.getEstat() != null ? lot.getEstat().name() : "",
                String.CASE_INSENSITIVE_ORDER
                );

            case "albara" ->
                Comparator.comparing(
                lot -> lot.getAlbaraProveidor() != null && lot.getAlbaraProveidor().getId() != null
                ? lot.getAlbaraProveidor().getId()
                : 0L
                );

            case "dataRecepcio" ->
                Comparator.comparing(
                lot -> lot.getAlbaraProveidor() != null && lot.getAlbaraProveidor().getDataRecepcio() != null
                ? lot.getAlbaraProveidor().getDataRecepcio()
                : LocalDate.MIN
                );

            default ->
                Comparator.comparing(
                lot -> lot.getAlbaraProveidor() != null && lot.getAlbaraProveidor().getDataRecepcio() != null
                ? lot.getAlbaraProveidor().getDataRecepcio()
                : LocalDate.MIN
                );
        };
    }

    /**
     * Executa l'operació `normalitzarTextCerca`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarTextCerca(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }

    /**
     * Executa l'operació `text`.
     *
     * @param value paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String text(String value) {
        return value == null ? "" : value.trim();
    }
}
