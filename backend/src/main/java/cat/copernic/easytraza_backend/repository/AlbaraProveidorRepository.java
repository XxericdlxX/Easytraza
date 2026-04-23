package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlbaraProveidorRepository extends JpaRepository<AlbaraProveidor, Long> {

    @Override
    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    List<AlbaraProveidor> findAll();

    @Override
    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    Optional<AlbaraProveidor> findById(Long id);

    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    List<AlbaraProveidor> findByProveidor_CifContainingIgnoreCase(String cif);

    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    List<AlbaraProveidor> findByDataRecepcio(LocalDate dataRecepcio);

    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    List<AlbaraProveidor> findByProveidor_CifContainingIgnoreCaseAndDataRecepcio(String cif, LocalDate dataRecepcio);
}
