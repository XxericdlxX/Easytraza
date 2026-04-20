package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Proveidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveidorRepository extends JpaRepository<Proveidor, String> {

    Optional<Proveidor> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
