package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Producte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProducteRepository extends JpaRepository<Producte, Long> {

    Optional<Producte> findByDescripcio(String descripcio);

    boolean existsByDescripcio(String descripcio);

    List<Producte> findByDescripcioContainingIgnoreCase(String text);
}
