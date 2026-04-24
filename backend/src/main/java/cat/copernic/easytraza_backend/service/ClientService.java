package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.ClientDto;
import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.ClientRepository;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(String nif) {
        return clientRepository.findById(nif);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public Client update(String nif, Client clientActualitzat) {
        Optional<Client> clientExistent = clientRepository.findById(nif);

        if (clientExistent.isPresent()) {
            Client client = clientExistent.get();
            client.setNom(clientActualitzat.getNom());
            client.setCognoms(clientActualitzat.getCognoms());
            client.setTipusClient(clientActualitzat.getTipusClient());
            client.setAdreca(clientActualitzat.getAdreca());
            client.setTelefon(clientActualitzat.getTelefon());
            client.setEmail(clientActualitzat.getEmail());
            client.setNotes(clientActualitzat.getNotes());
            return clientRepository.save(client);
        }

        return null;
    }

    public void deleteById(String nif) {
        clientRepository.deleteById(nif);
    }

    public String validarClient(ClientDto clientDto, String nifActual) {
        String document = normalitzarDocument(clientDto.getNif());

        if (document == null || document.isBlank()) {
            return "clients.nif.obligatori";
        }

        if (!esDocumentValid(document)) {
            return "clients.nif.invalid";
        }

        Optional<Client> clientAmbMateixDocument = clientRepository.findById(document);

        if (clientAmbMateixDocument.isPresent()) {
            if (nifActual == null || !clientAmbMateixDocument.get().getNif().equalsIgnoreCase(nifActual)) {
                return "clients.error.nif.duplicat";
            }
        }

        String emailNormalitzat = normalitzarEmail(clientDto.getEmail());
        if (emailNormalitzat != null && !emailNormalitzat.isBlank()) {
            Optional<Client> clientAmbMateixEmail = clientRepository.findByEmailIgnoreCase(emailNormalitzat);
            if (clientAmbMateixEmail.isPresent()) {
                if (nifActual == null || !clientAmbMateixEmail.get().getNif().equalsIgnoreCase(nifActual)) {
                    return "clients.error.email.duplicat";
                }
            }

            Optional<Usuari> usuariAmbMateixEmail = usuariRepository.findByEmailIgnoreCase(emailNormalitzat);
            if (usuariAmbMateixEmail.isPresent()) {
                return "clients.error.email.usuari";
            }
        }

        return null;
    }

    public Client convertirDtoAEntity(ClientDto clientDto) {
        Client client = new Client();
        client.setNif(normalitzarDocument(clientDto.getNif()));
        client.setNom(clientDto.getNom());
        client.setCognoms(buitANull(clientDto.getCognoms()));
        client.setTipusClient(clientDto.getTipusClient());
        client.setAdreca(clientDto.getAdreca());
        client.setTelefon(buitANull(clientDto.getTelefon()));
        client.setEmail(buitANull(normalitzarEmail(clientDto.getEmail())));
        client.setNotes(buitANull(clientDto.getNotes()));
        return client;
    }

    public ClientDto convertirEntityADto(Client client) {
        ClientDto clientDto = new ClientDto();
        clientDto.setNif(client.getNif());
        clientDto.setNom(client.getNom());
        clientDto.setCognoms(client.getCognoms());
        clientDto.setTipusClient(client.getTipusClient());
        clientDto.setAdreca(client.getAdreca());
        clientDto.setTelefon(client.getTelefon());
        clientDto.setEmail(client.getEmail());
        clientDto.setNotes(client.getNotes());
        return clientDto;
    }

    private String buitANull(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private String normalitzarDocument(String document) {
        if (document == null) {
            return null;
        }

        return document.trim().toUpperCase().replace(" ", "");
    }

    private String normalitzarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private boolean esDocumentValid(String document) {
        return esDniValid(document) || esNieValid(document) || esCifValid(document);
    }

    private boolean esDniValid(String dni) {
        if (!dni.matches("^\\d{8}[A-Z]$")) {
            return false;
        }

        String lletres = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(dni.substring(0, 8));
        char lletraCorrecta = lletres.charAt(numero % 23);

        return dni.charAt(8) == lletraCorrecta;
    }

    private boolean esNieValid(String nie) {
        if (!nie.matches("^[XYZ]\\d{7}[A-Z]$")) {
            return false;
        }

        String prefix = switch (nie.charAt(0)) {
            case 'X' ->
                "0";
            case 'Y' ->
                "1";
            case 'Z' ->
                "2";
            default ->
                "";
        };

        String dniEquivalent = prefix + nie.substring(1);
        return esDniValid(dniEquivalent);
    }

    private boolean esCifValid(String cif) {
        if (!cif.matches("^[ABCDEFGHJNPQRSUVW]\\d{7}[0-9A-J]$")) {
            return false;
        }

        char lletraInicial = cif.charAt(0);
        String digits = cif.substring(1, 8);
        char control = cif.charAt(8);

        int sumaParells = 0;
        int sumaSenars = 0;

        for (int i = 0; i < digits.length(); i++) {
            int digit = Character.getNumericValue(digits.charAt(i));

            if (i % 2 == 0) {
                int producte = digit * 2;
                sumaSenars += (producte / 10) + (producte % 10);
            } else {
                sumaParells += digit;
            }
        }

        int sumaTotal = sumaParells + sumaSenars;
        int unitat = sumaTotal % 10;
        int digitControl = (10 - unitat) % 10;
        char lletraControl = "JABCDEFGHI".charAt(digitControl);

        if ("PQRSNW".indexOf(lletraInicial) >= 0) {
            return control == lletraControl;
        }

        if ("ABEH".indexOf(lletraInicial) >= 0) {
            return control == Character.forDigit(digitControl, 10);
        }

        return control == Character.forDigit(digitControl, 10) || control == lletraControl;
    }
}
