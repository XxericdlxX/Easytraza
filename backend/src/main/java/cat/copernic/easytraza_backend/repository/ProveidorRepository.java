package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Proveidor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori de `ProveidorRepository` encarregat de l'accés a dades.
 */
@Repository
public interface ProveidorRepository extends JpaRepository<Proveidor, String> {

    /**
     * Executa l'operació `findByCifIgnoreCase`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Proveidor> findByCifIgnoreCase(String cif);

    /**
     * Executa l'operació `findByNomIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Proveidor> findByNomIgnoreCase(String nom);

    /**
     * Executa l'operació `findByEmailIgnoreCase`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Proveidor> findByEmailIgnoreCase(String email);

    /**
     * Executa l'operació `existsByCifIgnoreCase`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    boolean existsByCifIgnoreCase(String cif);

    /**
     * Executa l'operació `existsByEmailIgnoreCase`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Executa l'operació `findByNomContainingIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Proveidor> findByNomContainingIgnoreCase(String nom);

    /**
     * Executa l'operació `findByCifContainingIgnoreCase`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Proveidor> findByCifContainingIgnoreCase(String cif);

    /**
     * Executa l'operació
     * `findByCifContainingIgnoreCaseAndNomContainingIgnoreCase`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Proveidor> findByCifContainingIgnoreCaseAndNomContainingIgnoreCase(String cif, String nom);

    /**
     * Executa l'operació
     * `findByNomContainingIgnoreCaseOrCifContainingIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @param cif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Proveidor> findByNomContainingIgnoreCaseOrCifContainingIgnoreCase(String nom, String cif);

    /**
     * Executa l'operació
     * `findByCifContainingIgnoreCaseOrNomContainingIgnoreCase`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Proveidor> findByCifContainingIgnoreCaseOrNomContainingIgnoreCase(String cif, String nom);

    /**
     * Executa l'operació `findByEmailContainingIgnoreCase`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Proveidor> findByEmailContainingIgnoreCase(String email);

    /**
     * Executa l'operació `findByTelefonContainingIgnoreCase`.
     *
     * @param telefon paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Proveidor> findByTelefonContainingIgnoreCase(String telefon);
}
