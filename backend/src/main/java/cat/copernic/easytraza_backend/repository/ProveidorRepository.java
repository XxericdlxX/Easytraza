package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Proveidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveidorRepository extends JpaRepository<Proveidor, String> {
}
