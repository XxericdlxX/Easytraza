package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Comanda;
import cat.copernic.easytraza_backend.model.enums.EstatComanda;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositori encarregat de l'accés a dades de comandes.
 */
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    /**
     * Executa la consulta o cerca definida per `buscar`.
     *
     * @param clientNif paràmetre necessari per executar l'operació.
     * @param dataComanda paràmetre necessari per executar l'operació.
     * @param estat paràmetre necessari per executar l'operació.
     * @param producteId paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    /**
     * Executa la consulta o cerca definida per `findById`.
     *
     * @param id paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    /**
     * Executa la consulta o cerca definida per `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @Override
    @EntityGraph(attributePaths = {"client", "usuariCreador", "albaraClient", "linies", "linies.producte"})
    List<Comanda> findAll();

    @Override
    @EntityGraph(attributePaths = {"client", "usuariCreador", "albaraClient", "linies", "linies.producte"})
    Optional<Comanda> findById(Long id);

    @Query("""
           SELECT DISTINCT c
           FROM Comanda c
           LEFT JOIN c.linies l
           LEFT JOIN l.producte p
           WHERE (:clientNif IS NULL OR LOWER(c.client.nif) LIKE LOWER(CONCAT('%', :clientNif, '%')))
             AND (:dataComanda IS NULL OR c.dataComanda = :dataComanda)
             AND (:estat IS NULL OR c.estat = :estat)
             AND (:producteId IS NULL OR p.id = :producteId)
           ORDER BY c.dataComanda DESC, c.id DESC
           """)
    @EntityGraph(attributePaths = {"client", "usuariCreador", "albaraClient", "linies", "linies.producte"})
    List<Comanda> buscar(@Param("clientNif") String clientNif,
            @Param("dataComanda") LocalDate dataComanda,
            @Param("estat") EstatComanda estat,
            @Param("producteId") Long producteId);
}
