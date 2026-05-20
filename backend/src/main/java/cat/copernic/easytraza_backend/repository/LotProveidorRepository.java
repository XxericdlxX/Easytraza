package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositori de `LotProveidorRepository` encarregat de l'accés a dades.
 */
@Repository
public interface LotProveidorRepository extends JpaRepository<LotProveidor, Long> {

    /**
     * Executa l'operació `findByProveidor_CifAndCodiLotIgnoreCase`.
     *
     * @param proveidorCif paràmetre necessari per a l'operació.
     * @param codiLot paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<LotProveidor> findByProveidor_CifAndCodiLotIgnoreCase(String proveidorCif, String codiLot);

    @Query("""
           SELECT COUNT(lot)
           FROM LotProveidor lot
           WHERE LOWER(lot.albaraProveidor.proveidor.cif) = LOWER(:proveidorCif)
             AND lot.albaraProveidor.dataRecepcio = :dataRecepcio
             /**
              * Executa l'operació `LOWER`.
              * @param idActual paràmetre necessari per a l'operació.
              * @return resultat obtingut després d'executar l'operació.
              */
             AND LOWER(lot.codiLot) = LOWER(:codiLot)
             /**
              * Executa l'operació `AND`.
              * @param idActual paràmetre necessari per a l'operació.
              */
             AND (:idActual IS NULL OR lot.albaraProveidor.id <> :idActual)
           """)
    /**
     * Executa l'operació `countLotRepetitEnRecepcio`.
     *
     * @param proveidorCif paràmetre necessari per a l'operació.
     * @param dataRecepcio paràmetre necessari per a l'operació.
     * @param codiLot paràmetre necessari per a l'operació.
     * @param idActual paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    long countLotRepetitEnRecepcio(
            @Param("proveidorCif") String proveidorCif,
            @Param("dataRecepcio") LocalDate dataRecepcio,
            @Param("codiLot") String codiLot,
            @Param("idActual") Long idActual
    );

    /**
     * Executa l'operació `findByEstat`.
     *
     * @param estat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<LotProveidor> findByEstat(EstatLot estat);

    /**
     * Executa l'operació `findByMateriaPrima_Id`.
     *
     * @param materiaPrimaId paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<LotProveidor> findByMateriaPrima_Id(Long materiaPrimaId);

    /**
     * Executa l'operació `findByProveidor_CifContainingIgnoreCase`.
     *
     * @param proveidorCif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<LotProveidor> findByProveidor_CifContainingIgnoreCase(String proveidorCif);

    /**
     * Executa l'operació `findByMateriaPrima_IdAndEstat`.
     *
     * @param materiaPrimaId paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<LotProveidor> findByMateriaPrima_IdAndEstat(Long materiaPrimaId, EstatLot estat);

    @Query("""
           SELECT lot
           FROM LotProveidor lot
           WHERE (:codiLot IS NULL OR LOWER(lot.codiLot) LIKE LOWER(CONCAT('%', :codiLot, '%')))
             AND (:estat IS NULL OR lot.estat = :estat)
             /**
              * Executa l'operació `AND`.
              * @param DESC paràmetre necessari per a l'operació.
              * @param dataRecepcio paràmetre necessari per a l'operació.
              */
             AND (:materiaPrimaId IS NULL OR lot.materiaPrima.id = :materiaPrimaId)
             /**
              * Executa l'operació `AND`.
              * @param DESC paràmetre necessari per a l'operació.
              * @param dataRecepcio paràmetre necessari per a l'operació.
              */
             AND (:dataRecepcio IS NULL OR lot.albaraProveidor.dataRecepcio = :dataRecepcio)
           ORDER BY lot.albaraProveidor.dataRecepcio DESC, lot.id DESC
           """)
    /**
     * Executa l'operació `cercarLots`.
     *
     * @param codiLot paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @param materiaPrimaId paràmetre necessari per a l'operació.
     * @param dataRecepcio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<LotProveidor> cercarLots(
            @Param("codiLot") String codiLot,
            @Param("estat") EstatLot estat,
            @Param("materiaPrimaId") Long materiaPrimaId,
            @Param("dataRecepcio") LocalDate dataRecepcio
    );
}
