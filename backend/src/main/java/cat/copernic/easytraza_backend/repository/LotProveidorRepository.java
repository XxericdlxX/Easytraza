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
 * Repositori encarregat de l'accés a dades dels lots de proveïdor.
 */
@Repository
public interface LotProveidorRepository extends JpaRepository<LotProveidor, Long> {

    /**
     * Cerca un lot pel CIF del proveïdor i el codi del lot, ignorant majúscules
     * i minúscules.
     *
     * @param proveidorCif CIF/NIF/NIE del proveïdor.
     * @param codiLot codi identificador del lot.
     * @return lot trobat, si existeix.
     */
    Optional<LotProveidor> findByProveidor_CifAndCodiLotIgnoreCase(String proveidorCif, String codiLot);

    /**
     * Compta si ja existeix un lot repetit per al mateix proveïdor, data de
     * recepció i codi de lot.
     *
     * @param proveidorCif CIF/NIF/NIE del proveïdor.
     * @param dataRecepcio data de recepció de l'albarà.
     * @param codiLot codi del lot a validar.
     * @param idActual identificador de l'albarà actual, o null si és una alta
     * nova.
     * @return nombre de lots coincidents.
     */
    @Query("""
           SELECT COUNT(lot)
           FROM LotProveidor lot
           WHERE LOWER(lot.albaraProveidor.proveidor.cif) = LOWER(:proveidorCif)
             AND lot.albaraProveidor.dataRecepcio = :dataRecepcio
             AND LOWER(lot.codiLot) = LOWER(:codiLot)
             AND (:idActual IS NULL OR lot.albaraProveidor.id <> :idActual)
           """)
    long countLotRepetitEnRecepcio(
            @Param("proveidorCif") String proveidorCif,
            @Param("dataRecepcio") LocalDate dataRecepcio,
            @Param("codiLot") String codiLot,
            @Param("idActual") Long idActual
    );

    /**
     * Cerca lots per estat.
     *
     * @param estat estat del lot.
     * @return llista de lots amb l'estat indicat.
     */
    List<LotProveidor> findByEstat(EstatLot estat);

    /**
     * Cerca lots associats a una matèria primera.
     *
     * @param materiaPrimaId identificador de la matèria primera.
     * @return llista de lots de la matèria primera indicada.
     */
    List<LotProveidor> findByMateriaPrima_Id(Long materiaPrimaId);

    /**
     * Cerca lots pel CIF/NIF/NIE del proveïdor.
     *
     * @param proveidorCif text del document fiscal del proveïdor.
     * @return llista de lots coincidents.
     */
    List<LotProveidor> findByProveidor_CifContainingIgnoreCase(String proveidorCif);

    /**
     * Cerca lots d'una matèria primera amb un estat concret.
     *
     * @param materiaPrimaId identificador de la matèria primera.
     * @param estat estat del lot.
     * @return llista de lots coincidents.
     */
    List<LotProveidor> findByMateriaPrima_IdAndEstat(Long materiaPrimaId, EstatLot estat);

    /**
     * Cerca lots aplicant filtres opcionals.
     *
     * <p>
     * Tots els filtres poden ser null. Si {@code materiaPrimaId} és null, no
     * s'aplica cap filtre per matèria primera.</p>
     *
     * @param codiLot text del codi de lot a cercar, o null.
     * @param estat estat del lot, o null.
     * @param materiaPrimaId identificador de la matèria primera, o null.
     * @param dataRecepcio data de recepció, o null.
     * @return llista de lots coincidents amb els filtres.
     */
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
