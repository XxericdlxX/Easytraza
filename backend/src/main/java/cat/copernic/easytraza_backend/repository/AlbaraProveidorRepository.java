package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlbaraProveidorRepository extends JpaRepository<AlbaraProveidor, Long> {

    List<AlbaraProveidor> findByProveidor_CifContainingIgnoreCase(String cif);

    List<AlbaraProveidor> findByDataRecepcio(LocalDate dataRecepcio);

    List<AlbaraProveidor> findByProveidor_CifContainingIgnoreCaseAndDataRecepcio(String cif, LocalDate dataRecepcio);
}
