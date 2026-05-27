package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.Producte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositori de `ProducteRepository` encarregat de l'accés a dades.
 */
@Repository
public interface ProducteRepository extends JpaRepository<Producte, Long> {

    /**
     * Executa l'operació `findByNomIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<Producte> findByNomIgnoreCase(String nom);

    /**
     * Executa l'operació `existsByNomIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    boolean existsByNomIgnoreCase(String nom);

    /**
     * Executa l'operació
     * `findByNomContainingIgnoreCaseAndDescripcioContainingIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @param descripcio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    List<Producte> findByNomContainingIgnoreCaseAndDescripcioContainingIgnoreCase(String nom, String descripcio);
}
