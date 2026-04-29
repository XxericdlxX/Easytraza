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

    Optional<Proveidor> findByEmailIgnoreCase(String email);

    boolean existsByCifIgnoreCase(String cif);

    boolean existsByEmailIgnoreCase(String email);

    List<Proveidor> findByNomContainingIgnoreCase(String nom);

    List<Proveidor> findByCifContainingIgnoreCase(String cif);

    List<Proveidor> findByCifContainingIgnoreCaseAndNomContainingIgnoreCase(String cif, String nom);

    List<Proveidor> findByNomContainingIgnoreCaseOrCifContainingIgnoreCase(String nom, String cif);

    List<Proveidor> findByCifContainingIgnoreCaseOrNomContainingIgnoreCase(String cif, String nom);

    List<Proveidor> findByEmailContainingIgnoreCase(String email);

    List<Proveidor> findByTelefonContainingIgnoreCase(String telefon);
}
