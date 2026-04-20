package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotProveidorRepository extends JpaRepository<LotProveidor, Long> {

    Optional<LotProveidor> findByProveidor_CifAndCodiLotIgnoreCase(String proveidorCif, String codiLot);

    List<LotProveidor> findByEstat(EstatLot estat);

    List<LotProveidor> findByMateriaPrima_Id(Long materiaPrimaId);

    List<LotProveidor> findByProveidor_CifContainingIgnoreCase(String proveidorCif);
}
