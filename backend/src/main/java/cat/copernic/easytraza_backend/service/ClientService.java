package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.model.enums.TipusClient;
import cat.copernic.easytraza_backend.repository.ClientRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(String nif) {
        return clientRepository.findById(nif);
    }

    public Client save(Client client) {
        normalitzarClient(client);
        return clientRepository.save(client);
    }

    public Client update(String nif, Client clientActualitzat) {
        Optional<Client> clientExistentOpt = clientRepository.findById(nif);

        if (clientExistentOpt.isEmpty()) {
            return null;
        }

        Client clientExistent = clientExistentOpt.get();

        clientExistent.setNom(clientActualitzat.getNom());
        clientExistent.setTipusClient(clientActualitzat.getTipusClient());
        clientExistent.setAdreca(clientActualitzat.getAdreca());
        clientExistent.setTelefon(clientActualitzat.getTelefon());
        clientExistent.setEmail(clientActualitzat.getEmail());
        clientExistent.setNotes(clientActualitzat.getNotes());

        normalitzarClient(clientExistent);
        return clientRepository.save(clientExistent);
    }

    @Transactional
    public void deleteById(String nif) {
        clientRepository.deleteById(nif);
        clientRepository.flush();
    }

    public boolean existsById(String nif) {
        return clientRepository.existsById(nif);
    }

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

    public List<TipusClient> obtenirTipusClients() {
        return List.of(TipusClient.values());
    }

    private boolean tipusCoincideix(Client client, String tipus) {
        if (tipus.isBlank()) {
            return true;
        }

        if (client.getTipusClient() == null) {
            return false;
        }

        return client.getTipusClient().name().equalsIgnoreCase(tipus);
    }

    private boolean conte(String valor, String filtre) {
        if (filtre.isBlank()) {
            return true;
        }

        return valor != null && valor.toLowerCase().contains(filtre.toLowerCase());
    }

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
    }

    private String normalitzarDocument(String document) {
        return document == null ? null : document.trim().toUpperCase().replace(" ", "");
    }

    private String normalitzar(String text) {
        return text == null ? null : text.trim();
    }

    private String normalitzarOpcional(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }

    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }
}
