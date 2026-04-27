package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.AlbaraClient;
import cat.copernic.easytraza_backend.model.enums.EstatAlbaraClient;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
