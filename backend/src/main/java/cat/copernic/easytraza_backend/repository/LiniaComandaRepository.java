package cat.copernic.easytraza_backend.repository;

import cat.copernic.easytraza_backend.model.LiniaComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori encarregat de l'accés a dades de línies de comanda.
 */
@Repository
public interface LiniaComandaRepository extends JpaRepository<LiniaComanda, Long> {
}
