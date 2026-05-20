package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.AlbaraClient;
import cat.copernic.easytraza_backend.model.LiniaClient;
import cat.copernic.easytraza_backend.model.enums.EstatAlbaraClient;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositori de `AlbaraClientRepository` encarregat de l'accés a dades.
 */
@Repository
public interface AlbaraClientRepository extends JpaRepository<AlbaraClient, Long> {

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @Override
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findAll();

    /**
     * Executa l'operació `findById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Override
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari", "linies.lotsAssociats"})
    Optional<AlbaraClient> findById(Long id);

    /**
     * Executa l'operació `findByClient_NifContainingIgnoreCase`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByClient_NifContainingIgnoreCase(String nif);

    /**
     * Executa l'operació `findByDataProduccio`.
     *
     * @param dataProduccio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByDataProduccio(LocalDate dataProduccio);

    /**
     * Executa l'operació `findByEstat`.
     *
     * @param estat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByEstat(EstatAlbaraClient estat);

    /**
     * Executa l'operació
     * `findByClient_NifContainingIgnoreCaseAndDataProduccio`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @param dataProduccio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByClient_NifContainingIgnoreCaseAndDataProduccio(String nif, LocalDate dataProduccio);

    /**
     * Executa l'operació `findByClient_NifContainingIgnoreCaseAndEstat`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByClient_NifContainingIgnoreCaseAndEstat(String nif, EstatAlbaraClient estat);

    /**
     * Executa l'operació `findByDataProduccioAndEstat`.
     *
     * @param dataProduccio paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByDataProduccioAndEstat(LocalDate dataProduccio, EstatAlbaraClient estat);

    /**
     * Executa l'operació
     * `findByClient_NifContainingIgnoreCaseAndDataProduccioAndEstat`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @param dataProduccio paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByClient_NifContainingIgnoreCaseAndDataProduccioAndEstat(
            String nif,
            LocalDate dataProduccio,
            EstatAlbaraClient estat
    );

    @Query("""
           SELECT DISTINCT linia
           FROM LiniaClient linia
           JOIN FETCH linia.albaraClient albara
           JOIN FETCH albara.client client
           JOIN FETCH linia.producte producte
           LEFT JOIN FETCH linia.operari operari
           LEFT JOIN FETCH linia.lotsAssociats lots
           WHERE producte.id = :producteId
           ORDER BY albara.dataProduccio DESC, albara.id DESC, linia.id DESC
           """)
    /**
     * Executa l'operació `findLiniesProduccioByProducteId`.
     *
     * @param producteId paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<LiniaClient> findLiniesProduccioByProducteId(@Param("producteId") Long producteId);

    @Query("""
           SELECT DISTINCT linia
           FROM LiniaClient linia
           JOIN FETCH linia.albaraClient albara
           JOIN FETCH albara.client client
           JOIN FETCH linia.producte producte
           LEFT JOIN FETCH linia.operari operari
           LEFT JOIN FETCH linia.lotsAssociats lots
           ORDER BY albara.dataProduccio DESC, albara.id DESC, linia.id DESC
           """)
    /**
     * Executa l'operació `findTotesLiniesProduccioAmbLots`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    List<LiniaClient> findTotesLiniesProduccioAmbLots();

    @Query("""
           SELECT DISTINCT linia
           FROM LiniaClient linia
           JOIN FETCH linia.albaraClient albara
           JOIN FETCH albara.client client
           JOIN FETCH linia.producte producte
           LEFT JOIN FETCH linia.operari operari
           JOIN linia.lotsAssociats lotAssociat
           WHERE lotAssociat.id = :lotId
           ORDER BY albara.dataProduccio DESC, albara.id DESC, linia.id DESC
           """)
    /**
     * Executa l'operació `findLiniesProduccioByLotId`.
     *
     * @param lotId paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<LiniaClient> findLiniesProduccioByLotId(@Param("lotId") Long lotId);

    @Query("""
           SELECT FUNCTION('DAY', albara.dataProduccio), COALESCE(SUM(linia.quantitat), 0)
           FROM LiniaClient linia
           JOIN linia.albaraClient albara
           WHERE albara.estat = :estat
             AND albara.dataProduccio >= :dataInici
             AND albara.dataProduccio <= :dataFi
             /**
              * Executa l'operació `AND`.
              * @param producteId paràmetre necessari per a l'operació.
              */
             AND (:producteId IS NULL OR linia.producte.id = :producteId)
           /**
            * Executa l'operació `FUNCTION`.
            * @param producteId paràmetre necessari per a l'operació.
            * @return resultat obtingut després d'executar l'operació.
            */
           GROUP BY FUNCTION('DAY', albara.dataProduccio)
           /**
            * Executa l'operació `FUNCTION`.
            * @param producteId paràmetre necessari per a l'operació.
            * @return resultat obtingut després d'executar l'operació.
            */
           ORDER BY FUNCTION('DAY', albara.dataProduccio)
           """)
    /**
     * Executa l'operació `findQuantitatsVenudesAgrupadesPerDia`.
     *
     * @param estat paràmetre necessari per a l'operació.
     * @param dataInici paràmetre necessari per a l'operació.
     * @param dataFi paràmetre necessari per a l'operació.
     * @param producteId paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Object[]> findQuantitatsVenudesAgrupadesPerDia(
            @Param("estat") EstatAlbaraClient estat,
            @Param("dataInici") LocalDate dataInici,
            @Param("dataFi") LocalDate dataFi,
            @Param("producteId") Long producteId
    );

}
