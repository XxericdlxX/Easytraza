package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei `ProveidorService` del projecte EasyTraza.
 */
@Service
public class ProveidorService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.proveidors");

    @Autowired
    private ProveidorRepository proveidorRepository;

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<Proveidor> findAll() {
        return proveidorRepository.findAll();
    }

    /**
     * Executa l'operació `findById`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Optional<Proveidor> findById(String cif) {
        return proveidorRepository.findById(cif);
    }

    /**
     * Executa l'operació `save`.
     *
     * @param proveidor paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Proveidor save(Proveidor proveidor) {
        try {
            normalitzarProveidor(proveidor);
            Proveidor proveidorDesat = proveidorRepository.save(proveidor);
            LOGGER.info("Proveïdor desat correctament.");
            return proveidorDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en desar un proveïdor.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `update`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @param proveidorActualitzat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Proveidor update(String cif, Proveidor proveidorActualitzat) {
        Optional<Proveidor> proveidorExistentOpt = proveidorRepository.findById(cif);

        if (proveidorExistentOpt.isEmpty()) {
            LOGGER.warn("No s'ha pogut actualitzar el proveïdor perquè no existeix.");
            return null;
        }

        Proveidor proveidorExistent = proveidorExistentOpt.get();

        proveidorExistent.setNom(proveidorActualitzat.getNom());
        proveidorExistent.setAdreca(proveidorActualitzat.getAdreca());
        proveidorExistent.setTelefon(proveidorActualitzat.getTelefon());
        proveidorExistent.setEmail(proveidorActualitzat.getEmail());
        proveidorExistent.setNotes(proveidorActualitzat.getNotes());

        try {
            normalitzarProveidor(proveidorExistent);
            Proveidor proveidorDesat = proveidorRepository.save(proveidorExistent);
            LOGGER.info("Proveïdor actualitzat correctament.");
            return proveidorDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en actualitzar un proveïdor.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `deleteById`.
     *
     * @param cif paràmetre necessari per a l'operació.
     */
    public void deleteById(String cif) {
        try {
            proveidorRepository.deleteById(cif);
            LOGGER.info("Proveïdor eliminat correctament.");
        } catch (RuntimeException ex) {
            LOGGER.error("Error en eliminar un proveïdor.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `existsById`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean existsById(String cif) {
        return proveidorRepository.existsById(cif);
    }

    /**
     * Executa l'operació `buscar`.
     *
     * @param document paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param telefon paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<Proveidor> buscar(String document, String nom, String telefon, String email) {
        String documentNormalitzat = normalitzarTextCerca(document);
        String nomNormalitzat = normalitzarTextCerca(nom);
        String telefonNormalitzat = normalitzarTextCerca(telefon);
        String emailNormalitzat = normalitzarTextCerca(email);

        return findAll().stream()
                .filter(proveidor -> conte(proveidor.getCif(), documentNormalitzat))
                .filter(proveidor -> conte(proveidor.getNom(), nomNormalitzat))
                .filter(proveidor -> conte(proveidor.getTelefon(), telefonNormalitzat))
                .filter(proveidor -> conte(proveidor.getEmail(), emailNormalitzat))
                .toList();
    }

    /**
     * Executa l'operació `conte`.
     *
     * @param valor paràmetre necessari per a l'operació.
     * @param filtre paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private boolean conte(String valor, String filtre) {
        if (filtre.isBlank()) {
            return true;
        }

        return valor != null && valor.toLowerCase().contains(filtre.toLowerCase());
    }

    /**
     * Executa l'operació `normalitzarProveidor`.
     *
     * @param proveidor paràmetre necessari per a l'operació.
     */
    private void normalitzarProveidor(Proveidor proveidor) {
        if (proveidor == null) {
            return;
        }

        proveidor.setCif(normalitzarDocument(proveidor.getCif()));
        proveidor.setNom(normalitzar(proveidor.getNom()));
        proveidor.setAdreca(normalitzar(proveidor.getAdreca()));
        proveidor.setTelefon(normalitzarOpcional(proveidor.getTelefon()));
        proveidor.setEmail(normalitzarOpcional(proveidor.getEmail()));
        proveidor.setNotes(normalitzarOpcional(proveidor.getNotes()));
    }

    /**
     * Executa l'operació `normalitzarDocument`.
     *
     * @param document paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarDocument(String document) {
        return document == null ? null : document.trim().toUpperCase().replace(" ", "");
    }

    /**
     * Executa l'operació `normalitzar`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzar(String text) {
        return text == null ? null : text.trim();
    }

    /**
     * Executa l'operació `normalitzarOpcional`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarOpcional(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }

    /**
     * Executa l'operació `normalitzarTextCerca`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }
}
