package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.MateriaPrima;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori de `MateriaPrimaRepository` encarregat de l'accés a dades.
 */
@Repository
public interface MateriaPrimaRepository extends JpaRepository<MateriaPrima, Long> {

    /**
     * Executa l'operació `findByNom`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<MateriaPrima> findByNom(String nom);

    /**
     * Executa l'operació `findByNomIgnoreCase`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    Optional<MateriaPrima> findByNomIgnoreCase(String nom);

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
    List<MateriaPrima> findByNomContainingIgnoreCaseAndDescripcioContainingIgnoreCase(
            String nom,
            String descripcio
    );
}
