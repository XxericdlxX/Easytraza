package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import cat.copernic.easytraza_backend.repository.LotProveidorRepository;
import java.time.LocalDate;
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

    public List<LotProveidor> cercar(String codiLot, EstatLot estat, Long materiaPrimaId, LocalDate dataRecepcio) {
        return lotProveidorRepository.cercarLots(
                normalitzarTextCerca(codiLot),
                estat,
                materiaPrimaId,
                dataRecepcio
        );
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

    private String normalitzarTextCerca(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }
}
