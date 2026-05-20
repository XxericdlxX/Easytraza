package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositori de `AlbaraProveidorRepository` encarregat de l'accés a dades.
 */
@Repository
public interface AlbaraProveidorRepository extends JpaRepository<AlbaraProveidor, Long> {

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @Override
    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    List<AlbaraProveidor> findAll();

    /**
     * Executa l'operació `findById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Override
    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    Optional<AlbaraProveidor> findById(Long id);

    /**
     * Executa l'operació `findByProveidor_CifContainingIgnoreCase`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    List<AlbaraProveidor> findByProveidor_CifContainingIgnoreCase(String cif);

    /**
     * Executa l'operació `findByDataRecepcio`.
     *
     * @param dataRecepcio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    List<AlbaraProveidor> findByDataRecepcio(LocalDate dataRecepcio);

    /**
     * Executa l'operació
     * `findByProveidor_CifContainingIgnoreCaseAndDataRecepcio`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @param dataRecepcio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @EntityGraph(attributePaths = {"proveidor", "usuariReceptor", "lots", "lots.materiaPrima"})
    List<AlbaraProveidor> findByProveidor_CifContainingIgnoreCaseAndDataRecepcio(String cif, LocalDate dataRecepcio);
}
