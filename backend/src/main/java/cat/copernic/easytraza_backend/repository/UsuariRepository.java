package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariRepository extends JpaRepository<Usuari, Long> {

    Optional<Usuari> findByEmail(String email);

    boolean existsByEmail(String email);
}
