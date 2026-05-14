package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.Rol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariRepository extends JpaRepository<Usuari, Long> {

    Optional<Usuari> findByEmail(String email);

    Optional<Usuari> findByEmailIgnoreCase(String email);

    Optional<Usuari> findByTokenRecuperacioContrasenya(String tokenRecuperacioContrasenya);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuari> findByNomContainingIgnoreCaseAndCognomsContainingIgnoreCaseAndEmailContainingIgnoreCase(
            String nom,
            String cognoms,
            String email
    );

    List<Usuari> findByNomContainingIgnoreCaseAndCognomsContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRol(
            String nom,
            String cognoms,
            String email,
            Rol rol
    );
}
