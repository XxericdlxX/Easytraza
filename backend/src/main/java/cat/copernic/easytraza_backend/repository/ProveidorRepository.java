package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Proveidor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveidorRepository extends JpaRepository<Proveidor, String> {

    Optional<Proveidor> findByCifIgnoreCase(String cif);

    Optional<Proveidor> findByNomIgnoreCase(String nom);

    boolean existsByCifIgnoreCase(String cif);

    boolean existsByEmailIgnoreCase(String email);

    List<Proveidor> findByNomContainingIgnoreCase(String nom);

    List<Proveidor> findByCifContainingIgnoreCase(String cif);
}
