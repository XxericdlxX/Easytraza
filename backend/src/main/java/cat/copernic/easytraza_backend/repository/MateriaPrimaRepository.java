package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.MateriaPrima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {

    Optional<MateriaPrima> findByNom(String nom);

    boolean existsByNom(String nom);
}
