package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.UsuariDto;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.Rol;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuariService {

    private static final String SUPERADMIN_EMAIL = "superadmin@easytraza.local";

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private ProveidorRepository proveidorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuari> findAll() {
        return usuariRepository.findAll();
    }

    public Optional<Usuari> findById(Long id) {
        return usuariRepository.findById(id);
    }

    public List<Usuari> buscar(String nom, String cognoms, String email, Rol rol) {
        String nomNormalitzat = normalitzarTextCerca(nom);
        String cognomsNormalitzats = normalitzarTextCerca(cognoms);
        String emailNormalitzat = normalitzarTextCerca(email);

        if (rol == null) {
            return usuariRepository.findByNomContainingIgnoreCaseAndCognomsContainingIgnoreCaseAndEmailContainingIgnoreCase(
                    nomNormalitzat,
                    cognomsNormalitzats,
                    emailNormalitzat
            );
        }

        return usuariRepository.findByNomContainingIgnoreCaseAndCognomsContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRol(
                nomNormalitzat,
                cognomsNormalitzats,
                emailNormalitzat,
                rol
        );
    }

    public Usuari save(Usuari usuari) {
        codificarContrasenyaSiCal(usuari);
        return usuariRepository.save(usuari);
    }

    public Usuari update(Long id, Usuari usuariActualitzat) {
        Optional<Usuari> usuariExistent = usuariRepository.findById(id);

        if (usuariExistent.isPresent()) {
            Usuari usuari = usuariExistent.get();

            if (isProtectedUser(usuari)) {
                if (usuariActualitzat.getContrasenya() != null && !usuariActualitzat.getContrasenya().isBlank()) {
                    usuari.setContrasenya(passwordEncoder.encode(usuariActualitzat.getContrasenya()));
                }
                return usuariRepository.save(usuari);
            }

            usuari.setNom(usuariActualitzat.getNom());
            usuari.setCognoms(usuariActualitzat.getCognoms());
            usuari.setRol(usuariActualitzat.getRol());
            usuari.setEmail(usuari.getEmail());

            if (usuariActualitzat.getContrasenya() != null && !usuariActualitzat.getContrasenya().isBlank()) {
                usuari.setContrasenya(passwordEncoder.encode(usuariActualitzat.getContrasenya()));
            }

            return usuariRepository.save(usuari);
        } else {
            return null;
        }
    }

    public boolean deleteById(Long id) {
        Optional<Usuari> usuari = usuariRepository.findById(id);

        if (usuari.isPresent() && isProtectedUser(usuari.get())) {
            return false;
        }

        usuariRepository.deleteById(id);
        return true;
    }

    public boolean isProtectedUser(Usuari usuari) {
        return usuari != null && SUPERADMIN_EMAIL.equalsIgnoreCase(usuari.getEmail());
    }

    public boolean isProtectedUserById(Long id) {
        return usuariRepository.findById(id)
                .map(this::isProtectedUser)
                .orElse(false);
    }

    public String validarUsuari(UsuariDto usuariDto, Long idActual) {
        String emailNormalitzat = normalitzarEmail(usuariDto.getEmail());

        Optional<Usuari> usuariAmbMateixEmail = usuariRepository.findByEmailIgnoreCase(emailNormalitzat);

        if (usuariAmbMateixEmail.isPresent()) {
            if (idActual == null || !usuariAmbMateixEmail.get().getId().equals(idActual)) {
                return "usuaris.error.email.duplicat";
            }
        }

        Optional<Proveidor> proveidorAmbMateixEmail = proveidorRepository.findByEmailIgnoreCase(emailNormalitzat);
        if (proveidorAmbMateixEmail.isPresent()) {
            return "usuaris.error.email.proveidor";
        }

        if (idActual == null && (usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank())) {
            return "usuaris.contrasenya.obligatoria";
        }

        if (usuariDto.getRol() == Rol.ADMIN) {
            if (idActual == null) {
                if (usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank()) {
                    return "usuaris.admin.contrasenya.obligatoria";
                }
            } else {
                Optional<Usuari> usuariExistent = usuariRepository.findById(idActual);
                if (usuariExistent.isPresent()) {
                    String contrasenyaActual = usuariExistent.get().getContrasenya();
                    boolean novaContrasenyaBuida = usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank();
                    boolean contrasenyaActualBuida = contrasenyaActual == null || contrasenyaActual.isBlank();

                    if (novaContrasenyaBuida && contrasenyaActualBuida) {
                        return "usuaris.admin.contrasenya.obligatoria";
                    }
                }
            }
        }

        return null;
    }

    public Usuari convertirDtoAEntity(UsuariDto usuariDto) {
        Usuari usuari = new Usuari();
        usuari.setId(usuariDto.getId());
        usuari.setNom(normalitzarText(usuariDto.getNom()));
        usuari.setCognoms(normalitzarText(usuariDto.getCognoms()));
        usuari.setRol(usuariDto.getRol());
        usuari.setEmail(normalitzarEmail(usuariDto.getEmail()));
        usuari.setContrasenya(usuariDto.getContrasenya());
        return usuari;
    }

    public UsuariDto convertirEntityADto(Usuari usuari) {
        UsuariDto usuariDto = new UsuariDto();
        usuariDto.setId(usuari.getId());
        usuariDto.setNom(usuari.getNom());
        usuariDto.setCognoms(usuari.getCognoms());
        usuariDto.setRol(usuari.getRol());
        usuariDto.setEmail(usuari.getEmail());
        usuariDto.setContrasenya("");
        return usuariDto;
    }

    private void codificarContrasenyaSiCal(Usuari usuari) {
        if (usuari.getContrasenya() != null && !usuari.getContrasenya().isBlank()) {
            usuari.setContrasenya(passwordEncoder.encode(usuari.getContrasenya()));
        }
    }

    private String normalitzarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalitzarText(String text) {
        return text == null ? null : text.trim();
    }

    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }
}
