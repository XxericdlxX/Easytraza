package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.ProveidorDto;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveidorService {

    @Autowired
    private ProveidorRepository proveidorRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    public List<Proveidor> findAll() {
        return proveidorRepository.findAll();
    }

    public Optional<Proveidor> findById(String cif) {
        return proveidorRepository.findById(cif);
    }

    public Proveidor save(Proveidor proveidor) {
        return proveidorRepository.save(proveidor);
    }

    public Proveidor update(String cif, Proveidor proveidorActualitzat) {
        Optional<Proveidor> proveidorExistent = proveidorRepository.findById(cif);

        if (proveidorExistent.isPresent()) {
            Proveidor proveidor = proveidorExistent.get();
            proveidor.setNom(proveidorActualitzat.getNom());
            proveidor.setAdreca(proveidorActualitzat.getAdreca());
            proveidor.setNotes(proveidorActualitzat.getNotes());
            proveidor.setTelefon(proveidorActualitzat.getTelefon());
            proveidor.setEmail(proveidorActualitzat.getEmail());
            return proveidorRepository.save(proveidor);
        } else {
            return null;
        }
    }

    public void deleteById(String cif) {
        proveidorRepository.deleteById(cif);
    }

    public String validarProveidor(ProveidorDto proveidorDto, String cifActual) {
        String document = normalitzarDocument(proveidorDto.getCif());

        if (document == null || document.isBlank()) {
            return "proveidors.cif.obligatori";
        }

        if (!esDocumentValid(document)) {
            return "proveidors.cif.invalid";
        }

        Optional<Proveidor> proveidorAmbMateixDocument = proveidorRepository.findById(document);

        if (proveidorAmbMateixDocument.isPresent()) {
            if (cifActual == null || !proveidorAmbMateixDocument.get().getCif().equalsIgnoreCase(cifActual)) {
                return "proveidors.error.cif.duplicat";
            }
        }

        String emailNormalitzat = normalitzarEmail(proveidorDto.getEmail());
        if (emailNormalitzat != null && !emailNormalitzat.isBlank()) {
            Optional<Proveidor> proveidorAmbMateixEmail = proveidorRepository.findByEmailIgnoreCase(emailNormalitzat);
            if (proveidorAmbMateixEmail.isPresent()) {
                if (cifActual == null || !proveidorAmbMateixEmail.get().getCif().equalsIgnoreCase(cifActual)) {
                    return "proveidors.error.email.duplicat";
                }
            }

            Optional<Usuari> usuariAmbMateixEmail = usuariRepository.findByEmailIgnoreCase(emailNormalitzat);
            if (usuariAmbMateixEmail.isPresent()) {
                return "proveidors.error.email.usuari";
            }
        }

        return null;
    }

    public Proveidor convertirDtoAEntity(ProveidorDto proveidorDto) {
        Proveidor proveidor = new Proveidor();
        proveidor.setCif(normalitzarDocument(proveidorDto.getCif()));
        proveidor.setNom(proveidorDto.getNom());
        proveidor.setAdreca(proveidorDto.getAdreca());
        proveidor.setNotes(buitANull(proveidorDto.getNotes()));
        proveidor.setTelefon(buitANull(proveidorDto.getTelefon()));
        proveidor.setEmail(buitANull(normalitzarEmail(proveidorDto.getEmail())));
        return proveidor;
    }

    public ProveidorDto convertirEntityADto(Proveidor proveidor) {
        ProveidorDto proveidorDto = new ProveidorDto();
        proveidorDto.setCif(proveidor.getCif());
        proveidorDto.setNom(proveidor.getNom());
        proveidorDto.setAdreca(proveidor.getAdreca());
        proveidorDto.setNotes(proveidor.getNotes());
        proveidorDto.setTelefon(proveidor.getTelefon());
        proveidorDto.setEmail(proveidor.getEmail());
        return proveidorDto;
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
