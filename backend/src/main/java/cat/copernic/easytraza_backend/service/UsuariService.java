package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.config.IdentificadorFiscalValidator;
import cat.copernic.easytraza_backend.dto.PerfilUsuariDto;
import cat.copernic.easytraza_backend.dto.UsuariDto;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.Rol;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuariService {

    private static final String SUPERADMIN_EMAIL = "superadmin@easytraza.local";

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private ProveidorRepository proveidorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${perfil.fotos.path:../uploads/perfils}")
    private String perfilFotosPath;

    private static final Set<String> EXTENSIONS_FOTO_PERMESES = Set.of("jpg", "jpeg", "png", "webp");

    public List<Usuari> findAll() {
        return usuariRepository.findAll();
    }

    public Optional<Usuari> findById(Long id) {
        return usuariRepository.findById(id);
    }

    public Optional<Usuari> findByEmailIgnoreCase(String email) {
        return usuariRepository.findByEmailIgnoreCase(normalitzarEmail(email));
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

    @Transactional
    public Usuari crearUsuariAmbFoto(Usuari usuari, MultipartFile fotoPerfil) {
        Usuari usuariGuardat = save(usuari);
        actualitzarFotoPerfil(usuariGuardat.getId(), fotoPerfil);
        return usuariGuardat;
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

    public String validarPerfilUsuari(PerfilUsuariDto perfilUsuariDto, Long idActual) {
        String nifNormalitzat = normalitzarDocument(perfilUsuariDto.getNif());
        if (!IdentificadorFiscalValidator.esDocumentFiscalValid(nifNormalitzat)) {
            return "perfil.nif.invalid";
        }

        String emailNormalitzat = normalitzarEmail(perfilUsuariDto.getEmail());

        Optional<Usuari> usuariAmbMateixEmail = usuariRepository.findByEmailIgnoreCase(emailNormalitzat);
        if (usuariAmbMateixEmail.isPresent()
                && (idActual == null || !usuariAmbMateixEmail.get().getId().equals(idActual))) {
            return "perfil.error.email.duplicat";
        }

        Optional<Proveidor> proveidorAmbMateixEmail = proveidorRepository.findByEmailIgnoreCase(emailNormalitzat);
        if (proveidorAmbMateixEmail.isPresent()) {
            return "perfil.error.email.proveidor";
        }

        if (idActual == null) {
            return "perfil.error.no.trobat";
        }

        Optional<Usuari> usuariActual = usuariRepository.findById(idActual);
        if (usuariActual.isEmpty()) {
            return "perfil.error.no.trobat";
        }

        return validarCanviContrasenyaPerfil(perfilUsuariDto, usuariActual.get());
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

    public Usuari actualitzarPerfil(Long id, PerfilUsuariDto perfilUsuariDto) {
        Optional<Usuari> usuariExistent = usuariRepository.findById(id);
        if (usuariExistent.isEmpty()) {
            return null;
        }

        Usuari usuari = usuariExistent.get();
        usuari.setNom(normalitzarText(perfilUsuariDto.getNom()));
        usuari.setCognoms(normalitzarText(perfilUsuariDto.getCognoms()));
        usuari.setNif(normalitzarDocument(perfilUsuariDto.getNif()));

        if (!isProtectedUser(usuari)) {
            usuari.setEmail(normalitzarEmail(perfilUsuariDto.getEmail()));
        }

        if (hiHaCanviContrasenyaPerfil(perfilUsuariDto)) {
            usuari.setContrasenya(passwordEncoder.encode(perfilUsuariDto.getNovaContrasenya()));
        }

        return usuariRepository.save(usuari);
    }

    public String validarFotoPerfil(MultipartFile fotoPerfil) {
        if (fotoPerfil == null || fotoPerfil.isEmpty()) {
            return null;
        }

        String contentType = fotoPerfil.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return "perfil.foto.format.invalid";
        }

        String extensio = obtenirExtensioFoto(fotoPerfil.getOriginalFilename());
        if (!EXTENSIONS_FOTO_PERMESES.contains(extensio)) {
            return "perfil.foto.format.invalid";
        }

        return null;
    }

    public void actualitzarFotoPerfil(Long id, MultipartFile fotoPerfil) {
        if (fotoPerfil == null || fotoPerfil.isEmpty()) {
            return;
        }

        Usuari usuari = usuariRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("perfil.error.no.trobat"));

        String extensio = obtenirExtensioFoto(fotoPerfil.getOriginalFilename());
        String nomFitxer = "perfil-" + id + "-" + UUID.randomUUID() + "." + extensio;

        try {
            Path directori = Paths.get(perfilFotosPath).toAbsolutePath().normalize();
            Files.createDirectories(directori);

            Path desti = directori.resolve(nomFitxer).normalize();
            if (!desti.startsWith(directori)) {
                throw new IllegalStateException("perfil.foto.error.guardar");
            }

            try (InputStream input = fotoPerfil.getInputStream()) {
                Files.copy(input, desti, StandardCopyOption.REPLACE_EXISTING);
            }

            usuari.setFotoPerfilNom(nomFitxer);
            usuariRepository.save(usuari);
        } catch (IOException ex) {
            throw new IllegalStateException("perfil.foto.error.guardar", ex);
        }
    }

    public Optional<Resource> carregarFotoPerfil(String nomFitxer) {
        if (nomFitxer == null || nomFitxer.isBlank()) {
            return Optional.empty();
        }

        try {
            Path directori = Paths.get(perfilFotosPath).toAbsolutePath().normalize();
            Path fitxer = directori.resolve(nomFitxer).normalize();

            if (!fitxer.startsWith(directori) || !Files.exists(fitxer) || !Files.isReadable(fitxer)) {
                return Optional.empty();
            }

            Resource resource = new UrlResource(fitxer.toUri());
            return resource.exists() && resource.isReadable() ? Optional.of(resource) : Optional.empty();
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private String validarCanviContrasenyaPerfil(PerfilUsuariDto perfilUsuariDto, Usuari usuariActual) {
        if (!hiHaCanviContrasenyaPerfil(perfilUsuariDto)) {
            return null;
        }

        if (esBuit(perfilUsuariDto.getContrasenyaActual())) {
            return "perfil.contrasenya.actual.obligatoria";
        }

        if (esBuit(perfilUsuariDto.getNovaContrasenya())) {
            return "perfil.contrasenya.nova.obligatoria";
        }

        if (esBuit(perfilUsuariDto.getConfirmarContrasenya())) {
            return "perfil.contrasenya.confirmar.obligatoria";
        }

        if (!perfilUsuariDto.getNovaContrasenya().equals(perfilUsuariDto.getConfirmarContrasenya())) {
            return "perfil.error.contrasenyes.no.coincideixen";
        }

        if (usuariActual.getContrasenya() == null
                || usuariActual.getContrasenya().isBlank()
                || !passwordEncoder.matches(perfilUsuariDto.getContrasenyaActual(), usuariActual.getContrasenya())) {
            return "perfil.error.contrasenya.actual.incorrecta";
        }

        return null;
    }

    private boolean hiHaCanviContrasenyaPerfil(PerfilUsuariDto perfilUsuariDto) {
        return !esBuit(perfilUsuariDto.getContrasenyaActual())
                || !esBuit(perfilUsuariDto.getNovaContrasenya())
                || !esBuit(perfilUsuariDto.getConfirmarContrasenya());
    }

    private boolean esBuit(String valor) {
        return valor == null || valor.isBlank();
    }

    public PerfilUsuariDto convertirEntityAPerfilDto(Usuari usuari) {
        PerfilUsuariDto perfilUsuariDto = new PerfilUsuariDto();
        perfilUsuariDto.setId(usuari.getId());
        perfilUsuariDto.setNom(usuari.getNom());
        perfilUsuariDto.setCognoms(usuari.getCognoms());
        perfilUsuariDto.setNif(usuari.getNif());
        perfilUsuariDto.setEmail(usuari.getEmail());
        perfilUsuariDto.setFotoPerfilNom(usuari.getFotoPerfilNom());
        return perfilUsuariDto;
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

    private String obtenirExtensioFoto(String nomOriginal) {
        if (nomOriginal == null || !nomOriginal.contains(".")) {
            return "jpg";
        }

        return nomOriginal.substring(nomOriginal.lastIndexOf('.') + 1)
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalitzarDocument(String document) {
        return IdentificadorFiscalValidator.normalitzar(document);
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
