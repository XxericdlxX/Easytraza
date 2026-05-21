package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.Rol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori de `UsuariRepository` encarregat de l'accés a dades.
 */
@Repository
public interface UsuariRepository extends JpaRepository<Usuari, Long> {

    /**
     * Executa l'operació `findByEmail`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Usuari> findByEmail(String email);

    /**
     * Executa l'operació `findByEmailIgnoreCase`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Usuari> findByEmailIgnoreCase(String email);

    /**
     * Executa l'operació `findByTokenRecuperacioContrasenya`.
     *
     * @param tokenRecuperacioContrasenya paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Usuari> findByTokenRecuperacioContrasenya(String tokenRecuperacioContrasenya);

    /**
     * Executa l'operació `existsByEmail`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    boolean existsByEmail(String email);

    /**
     * Executa l'operació `existsByEmailIgnoreCase`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Executa l'operació
     * `findByNomContainingIgnoreCaseAndCognomsContainingIgnoreCaseAndEmailContainingIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @param cognoms paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Usuari> findByNomContainingIgnoreCaseAndCognomsContainingIgnoreCaseAndEmailContainingIgnoreCase(
            String nom,
            String cognoms,
            String email
    );

    /**
     * Executa l'operació
     * `findByNomContainingIgnoreCaseAndCognomsContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRol`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @param cognoms paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @param rol paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Usuari> findByNomContainingIgnoreCaseAndCognomsContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRol(
            String nom,
            String cognoms,
            String email,
            Rol rol
    );
}
