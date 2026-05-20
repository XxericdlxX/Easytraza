package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositori de `ClientRepository` encarregat de l'accés a dades.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    /**
     * Executa l'operació `findByEmailIgnoreCase`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Client> findByEmailIgnoreCase(String email);

    /**
     * Executa l'operació `existsByEmailIgnoreCase`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Executa l'operació `findByNomIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Client> findByNomIgnoreCase(String nom);
}
