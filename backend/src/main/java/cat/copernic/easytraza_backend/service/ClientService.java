package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.model.enums.TipusClient;
import cat.copernic.easytraza_backend.repository.ClientRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei `ClientService` del projecte EasyTraza.
 */
@Service
public class ClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.clients");

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    /**
     * Executa l'operació `findById`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Optional<Client> findById(String nif) {
        return clientRepository.findById(nif);
    }

    /**
     * Executa l'operació `save`.
     *
     * @param client paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Client save(Client client) {
        try {
            normalitzarClient(client);
            Client clientDesat = clientRepository.save(client);
            LOGGER.info("Client desat correctament.");
            return clientDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en desar un client.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `update`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @param clientActualitzat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Client update(String nif, Client clientActualitzat) {
        Optional<Client> clientExistentOpt = clientRepository.findById(nif);

        if (clientExistentOpt.isEmpty()) {
            LOGGER.warn("No s'ha pogut actualitzar el client perquè no existeix.");
            return null;
        }

        Client clientExistent = clientExistentOpt.get();

        clientExistent.setNom(clientActualitzat.getNom());
        clientExistent.setTipusClient(clientActualitzat.getTipusClient());
        clientExistent.setTipusClientAltres(clientActualitzat.getTipusClientAltres());
        clientExistent.setAdreca(clientActualitzat.getAdreca());
        clientExistent.setTelefon(clientActualitzat.getTelefon());
        clientExistent.setEmail(clientActualitzat.getEmail());
        clientExistent.setNotes(clientActualitzat.getNotes());

        try {
            normalitzarClient(clientExistent);
            Client clientDesat = clientRepository.save(clientExistent);
            LOGGER.info("Client actualitzat correctament.");
            return clientDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en actualitzar un client.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `deleteById`.
     *
     * @param nif paràmetre necessari per a l'operació.
     */
    @Transactional
    public void deleteById(String nif) {
        try {
            clientRepository.deleteById(nif);
            clientRepository.flush();
            LOGGER.info("Client eliminat correctament.");
        } catch (RuntimeException ex) {
            LOGGER.error("Error en eliminar un client.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `existsById`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean existsById(String nif) {
        return clientRepository.existsById(nif);
    }

    /**
     * Executa l'operació `buscar`.
     *
     * @param document paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param tipus paràmetre necessari per a l'operació.
     * @param telefon paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<Client> buscar(String document, String nom, String tipus, String telefon, String email) {
        String documentNormalitzat = normalitzarTextCerca(document);
        String nomNormalitzat = normalitzarTextCerca(nom);
        String tipusNormalitzat = normalitzarTextCerca(tipus);
        String telefonNormalitzat = normalitzarTextCerca(telefon);
        String emailNormalitzat = normalitzarTextCerca(email);

        return findAll().stream()
                .filter(client -> conte(client.getNif(), documentNormalitzat))
                .filter(client -> conte(client.getNom(), nomNormalitzat))
                .filter(client -> tipusCoincideix(client, tipusNormalitzat))
                .filter(client -> conte(client.getTelefon(), telefonNormalitzat))
                .filter(client -> conte(client.getEmail(), emailNormalitzat))
                .collect(Collectors.toList());
    }

    /**
     * Executa l'operació `obtenirTipusClients`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<TipusClient> obtenirTipusClients() {
        return List.of(TipusClient.values());
    }

    /**
     * Executa l'operació `obtenirTextTipusClient`.
     *
     * @param client paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public String obtenirTextTipusClient(Client client) {
        if (client == null || client.getTipusClient() == null) {
            return "-";
        }

        if (client.getTipusClient() == TipusClient.ALTRES
                && client.getTipusClientAltres() != null
                && !client.getTipusClientAltres().isBlank()) {
            return client.getTipusClientAltres();
        }

        return client.getTipusClient().name();
    }

    /**
     * Executa l'operació `tipusCoincideix`.
     *
     * @param client paràmetre necessari per a l'operació.
     * @param tipus paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private boolean tipusCoincideix(Client client, String tipus) {
        if (tipus.isBlank()) {
            return true;
        }

        if (client.getTipusClient() == null) {
            return false;
        }

        return client.getTipusClient().name().equalsIgnoreCase(tipus)
                || conte(client.getTipusClientAltres(), tipus);
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
     * Executa l'operació `normalitzarClient`.
     *
     * @param client paràmetre necessari per a l'operació.
     */
    private void normalitzarClient(Client client) {
        if (client == null) {
            return;
        }

        client.setNif(normalitzarDocument(client.getNif()));
        client.setNom(normalitzar(client.getNom()));
        client.setAdreca(normalitzar(client.getAdreca()));
        client.setTelefon(normalitzarOpcional(client.getTelefon()));
        client.setEmail(normalitzarOpcional(client.getEmail()));
        client.setNotes(normalitzarOpcional(client.getNotes()));
        client.setTipusClientAltres(normalitzarOpcional(client.getTipusClientAltres()));

        if (client.getTipusClient() != TipusClient.ALTRES) {
            client.setTipusClientAltres(null);
        }
    }

    /**
     * Executa l'operació `normalitzarDocument`.
     *
     * @param document paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarDocument(String document) {
        return document == null ? null : document.trim().toUpperCase().replace(" ", "").replace("-", "");
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
