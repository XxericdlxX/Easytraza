package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotProveidorRepository extends JpaRepository<LotProveidor, Long> {

    Optional<LotProveidor> findByProveidor_CifAndCodiLotIgnoreCase(String proveidorCif, String codiLot);

    List<LotProveidor> findByEstat(EstatLot estat);

    List<LotProveidor> findByMateriaPrima_Id(Long materiaPrimaId);

    List<LotProveidor> findByProveidor_CifContainingIgnoreCase(String proveidorCif);

    List<LotProveidor> findByMateriaPrima_IdAndEstat(Long materiaPrimaId, EstatLot estat);

    @Query("""
           SELECT lot
           FROM LotProveidor lot
           WHERE (:codiLot IS NULL OR LOWER(lot.codiLot) LIKE LOWER(CONCAT('%', :codiLot, '%')))
             AND (:estat IS NULL OR lot.estat = :estat)
             AND (:materiaPrimaId IS NULL OR lot.materiaPrima.id = :materiaPrimaId)
             AND (:dataRecepcio IS NULL OR lot.albaraProveidor.dataRecepcio = :dataRecepcio)
           ORDER BY lot.albaraProveidor.dataRecepcio DESC, lot.id DESC
           """)
    List<LotProveidor> cercarLots(
            @Param("codiLot") String codiLot,
            @Param("estat") EstatLot estat,
            @Param("materiaPrimaId") Long materiaPrimaId,
            @Param("dataRecepcio") LocalDate dataRecepcio
    );
}
