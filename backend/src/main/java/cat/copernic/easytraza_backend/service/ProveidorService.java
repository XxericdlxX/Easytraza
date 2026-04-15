package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.ProveidorDto;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveidorService {

    @Autowired
    private ProveidorRepository proveidorRepository;

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
            proveidor.setDescripcio(proveidorActualitzat.getDescripcio());
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
            return "El CIF/NIF/NIE és obligatori";
        }

        if (!esDocumentValid(document)) {
            return "El CIF/NIF/NIE no té un format vàlid";
        }

        Optional<Proveidor> proveidorAmbMateixDocument = proveidorRepository.findById(document);

        if (proveidorAmbMateixDocument.isPresent()) {
            if (cifActual == null || !proveidorAmbMateixDocument.get().getCif().equalsIgnoreCase(cifActual)) {
                return "Ja existeix un proveïdor amb aquest CIF/NIF/NIE";
            }
        }

        return null;
    }

    public Proveidor convertirDtoAEntity(ProveidorDto proveidorDto) {
        Proveidor proveidor = new Proveidor();
        proveidor.setCif(normalitzarDocument(proveidorDto.getCif()));
        proveidor.setNom(proveidorDto.getNom());
        proveidor.setAdreca(proveidorDto.getAdreca());
        proveidor.setDescripcio(proveidorDto.getDescripcio());
        return proveidor;
    }

    public ProveidorDto convertirEntityADto(Proveidor proveidor) {
        ProveidorDto proveidorDto = new ProveidorDto();
        proveidorDto.setCif(proveidor.getCif());
        proveidorDto.setNom(proveidor.getNom());
        proveidorDto.setAdreca(proveidor.getAdreca());
        proveidorDto.setDescripcio(proveidor.getDescripcio());
        return proveidorDto;
    }

    private String normalitzarDocument(String document) {
        if (document == null) {
            return null;
        }
        return document.trim().toUpperCase().replace(" ", "");
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
