package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import cat.copernic.easytraza_backend.repository.LotProveidorRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LotProveidorService {

    @Autowired
    private LotProveidorRepository lotProveidorRepository;

    public List<LotProveidor> findAll() {
        return lotProveidorRepository.findAll();
    }

    public Optional<LotProveidor> findById(Long id) {
        return lotProveidorRepository.findById(id);
    }

    public boolean existeixLotObert() {
        return !lotProveidorRepository.findByEstat(EstatLot.OBERT).isEmpty();
    }

    public List<LotProveidor> cercar(String codiLot, EstatLot estat, Long materiaPrimaId, LocalDate dataRecepcio) {
        return cercar(codiLot, estat, materiaPrimaId, dataRecepcio, "dataRecepcio", "desc");
    }

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

    @Transactional
    public LotProveidor iniciarLot(Long id) {
        LotProveidor lot = lotProveidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("lots.error.no.trobat"));

        if (lot.getEstat() == EstatLot.ACABAT) {
            throw new IllegalStateException("lots.error.iniciar.acabat");
        }

        if (lot.getEstat() == EstatLot.OBERT) {
            throw new IllegalStateException("lots.error.iniciar.obert");
        }

        if (lot.getMateriaPrima() == null || lot.getMateriaPrima().getId() == null) {
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
            }
        }

        lot.setEstat(EstatLot.OBERT);
        lot.setDataObertura(avui);
        lot.setDataAcabament(null);

        return lotProveidorRepository.save(lot);
    }

    @Transactional
    public LotProveidor finalitzarLot(Long id) {
        LotProveidor lot = lotProveidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("lots.error.no.trobat"));

        if (lot.getEstat() == EstatLot.EN_ESTOC) {
            throw new IllegalStateException("lots.error.finalitzar.estoc");
        }

        if (lot.getEstat() == EstatLot.ACABAT) {
            throw new IllegalStateException("lots.error.finalitzar.acabat");
        }

        lot.setEstat(EstatLot.ACABAT);
        lot.setDataAcabament(LocalDate.now());

        return lotProveidorRepository.save(lot);
    }

    private List<LotProveidor> ordenarLots(List<LotProveidor> lots, String sortField, String sortDir) {
        Comparator<LotProveidor> comparador = obtenirComparador(sortField);

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparador = comparador.reversed();
        }

        return lots.stream()
                .sorted(comparador)
                .toList();
    }

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

    private String normalitzarTextCerca(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }
}
