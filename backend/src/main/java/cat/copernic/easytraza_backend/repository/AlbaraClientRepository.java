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

@Repository
public interface AlbaraClientRepository extends JpaRepository<AlbaraClient, Long> {

    @Override
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findAll();

    @Override
    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari", "linies.lotsAssociats"})
    Optional<AlbaraClient> findById(Long id);

    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByClient_NifContainingIgnoreCase(String nif);

    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByDataProduccio(LocalDate dataProduccio);

    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByEstat(EstatAlbaraClient estat);

    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByClient_NifContainingIgnoreCaseAndDataProduccio(String nif, LocalDate dataProduccio);

    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByClient_NifContainingIgnoreCaseAndEstat(String nif, EstatAlbaraClient estat);

    @EntityGraph(attributePaths = {"client", "usuariCreador", "linies", "linies.producte", "linies.operari"})
    List<AlbaraClient> findByDataProduccioAndEstat(LocalDate dataProduccio, EstatAlbaraClient estat);

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
    List<LiniaClient> findLiniesProduccioByLotId(@Param("lotId") Long lotId);

    @Query("""
           SELECT FUNCTION('DAY', albara.dataProduccio), COALESCE(SUM(linia.quantitat), 0)
           FROM LiniaClient linia
           JOIN linia.albaraClient albara
           WHERE albara.estat = :estat
             AND albara.dataProduccio >= :dataInici
             AND albara.dataProduccio <= :dataFi
             AND (:producteId IS NULL OR linia.producte.id = :producteId)
           GROUP BY FUNCTION('DAY', albara.dataProduccio)
           ORDER BY FUNCTION('DAY', albara.dataProduccio)
           """)
    List<Object[]> findQuantitatsVenudesAgrupadesPerDia(
            @Param("estat") EstatAlbaraClient estat,
            @Param("dataInici") LocalDate dataInici,
            @Param("dataFi") LocalDate dataFi,
            @Param("producteId") Long producteId
    );

}
