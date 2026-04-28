package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.MateriaPrima;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {

    Optional<MateriaPrima> findByNom(String nom);

    Optional<MateriaPrima> findByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCase(String nom);

    List<MateriaPrima> findByNomContainingIgnoreCaseAndDescripcioContainingIgnoreCase(
            String nom,
            String descripcio
    );
}
