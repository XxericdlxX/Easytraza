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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servei `UsuariService` del projecte EasyTraza.
 */
@Service
public class UsuariService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.usuaris");

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

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<Usuari> findAll() {
        return usuariRepository.findAll();
    }

    /**
     * Executa l'operació `findById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Optional<Usuari> findById(Long id) {
        return usuariRepository.findById(id);
    }

    /**
     * Executa l'operació `findByEmailIgnoreCase`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Optional<Usuari> findByEmailIgnoreCase(String email) {
        return usuariRepository.findByEmailIgnoreCase(normalitzarEmail(email));
    }

    /**
     * Executa l'operació `buscar`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @param cognoms paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @param rol paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `save`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Usuari save(Usuari usuari) {
        try {
            codificarContrasenyaSiCal(usuari);
            Usuari usuariDesat = usuariRepository.save(usuari);
            LOGGER.info("Usuari desat correctament amb id {}.", usuariDesat.getId());
            return usuariDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en desar un usuari.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `crearUsuariAmbFoto`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @param fotoPerfil paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Transactional
    public Usuari crearUsuariAmbFoto(Usuari usuari, MultipartFile fotoPerfil) {
        Usuari usuariGuardat = save(usuari);
        actualitzarFotoPerfil(usuariGuardat.getId(), fotoPerfil);
        LOGGER.info("Usuari creat amb foto de perfil amb id {}.", usuariGuardat.getId());
        return usuariGuardat;
    }

    /**
     * Executa l'operació `update`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param usuariActualitzat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Usuari update(Long id, Usuari usuariActualitzat) {
        Optional<Usuari> usuariExistent = usuariRepository.findById(id);

        if (usuariExistent.isPresent()) {
            Usuari usuari = usuariExistent.get();

            if (isProtectedUser(usuari)) {
                if (usuariActualitzat.getContrasenya() != null && !usuariActualitzat.getContrasenya().isBlank()) {
                    usuari.setContrasenya(passwordEncoder.encode(usuariActualitzat.getContrasenya()));
                }
                try {
                    Usuari usuariDesat = usuariRepository.save(usuari);
                    LOGGER.info("Usuari protegit actualitzat amb id {}.", usuariDesat.getId());
                    return usuariDesat;
                } catch (RuntimeException ex) {
                    LOGGER.error("Error en actualitzar l'usuari protegit.", ex);
                    throw ex;
                }
            }

            usuari.setNom(usuariActualitzat.getNom());
            usuari.setCognoms(usuariActualitzat.getCognoms());
            usuari.setRol(usuariActualitzat.getRol());
            usuari.setEmail(usuari.getEmail());

            if (usuariActualitzat.getContrasenya() != null && !usuariActualitzat.getContrasenya().isBlank()) {
                usuari.setContrasenya(passwordEncoder.encode(usuariActualitzat.getContrasenya()));
            }

            try {
                Usuari usuariDesat = usuariRepository.save(usuari);
                LOGGER.info("Usuari actualitzat correctament amb id {}.", usuariDesat.getId());
                return usuariDesat;
            } catch (RuntimeException ex) {
                LOGGER.error("Error en actualitzar un usuari.", ex);
                throw ex;
            }
        } else {
            LOGGER.warn("No s'ha pogut actualitzar l'usuari perquè no existeix.");
            return null;
        }
    }

    /**
     * Executa l'operació `deleteById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean deleteById(Long id) {
        Optional<Usuari> usuari = usuariRepository.findById(id);

        if (usuari.isPresent() && isProtectedUser(usuari.get())) {
            LOGGER.warn("S'ha bloquejat l'eliminació de l'usuari protegit amb id {}.", id);
            return false;
        }

        try {
            usuariRepository.deleteById(id);
            LOGGER.info("Usuari eliminat correctament amb id {}.", id);
            return true;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en eliminar l'usuari amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `isProtectedUser`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean isProtectedUser(Usuari usuari) {
        return usuari != null && SUPERADMIN_EMAIL.equalsIgnoreCase(usuari.getEmail());
    }

    /**
     * Executa l'operació `isProtectedUserById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean isProtectedUserById(Long id) {
        return usuariRepository.findById(id)
                .map(this::isProtectedUser)
                .orElse(false);
    }

    /**
     * Executa l'operació `validarPerfilUsuari`.
     *
     * @param perfilUsuariDto paràmetre necessari per a l'operació.
     * @param idActual paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public String validarPerfilUsuari(PerfilUsuariDto perfilUsuariDto, Long idActual) {
        String nifNormalitzat = normalitzarDocument(perfilUsuariDto.getNif());
        if (!IdentificadorFiscalValidator.esDocumentFiscalValid(nifNormalitzat)) {
            LOGGER.warn("Validació de perfil rebutjada per document fiscal no vàlid.");
            return "perfil.nif.invalid";
        }

        String emailNormalitzat = normalitzarEmail(perfilUsuariDto.getEmail());

        Optional<Usuari> usuariAmbMateixEmail = usuariRepository.findByEmailIgnoreCase(emailNormalitzat);
        if (usuariAmbMateixEmail.isPresent()
                && (idActual == null || !usuariAmbMateixEmail.get().getId().equals(idActual))) {
            LOGGER.warn("Validació de perfil rebutjada per correu duplicat.");
            return "perfil.error.email.duplicat";
        }

        Optional<Proveidor> proveidorAmbMateixEmail = proveidorRepository.findByEmailIgnoreCase(emailNormalitzat);
        if (proveidorAmbMateixEmail.isPresent()) {
            LOGGER.warn("Validació de perfil rebutjada perquè el correu pertany a un proveïdor.");
            return "perfil.error.email.proveidor";
        }

        if (idActual == null) {
            LOGGER.warn("Validació de perfil rebutjada perquè no s'ha trobat l'usuari.");
            return "perfil.error.no.trobat";
        }

        Optional<Usuari> usuariActual = usuariRepository.findById(idActual);
        if (usuariActual.isEmpty()) {
            LOGGER.warn("Validació de perfil rebutjada perquè l'usuari actual no existeix.");
            return "perfil.error.no.trobat";
        }

        return validarCanviContrasenyaPerfil(perfilUsuariDto, usuariActual.get());
    }

    /**
     * Executa l'operació `validarUsuari`.
     *
     * @param usuariDto paràmetre necessari per a l'operació.
     * @param idActual paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public String validarUsuari(UsuariDto usuariDto, Long idActual) {
        String emailNormalitzat = normalitzarEmail(usuariDto.getEmail());

        Optional<Usuari> usuariAmbMateixEmail = usuariRepository.findByEmailIgnoreCase(emailNormalitzat);

        if (usuariAmbMateixEmail.isPresent()) {
            if (idActual == null || !usuariAmbMateixEmail.get().getId().equals(idActual)) {
                LOGGER.warn("Validació d'usuari rebutjada per correu duplicat.");
                return "usuaris.error.email.duplicat";
            }
        }

        Optional<Proveidor> proveidorAmbMateixEmail = proveidorRepository.findByEmailIgnoreCase(emailNormalitzat);
        if (proveidorAmbMateixEmail.isPresent()) {
            LOGGER.warn("Validació d'usuari rebutjada perquè el correu pertany a un proveïdor.");
            return "usuaris.error.email.proveidor";
        }

        if (idActual == null && (usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank())) {
            LOGGER.warn("Validació d'usuari rebutjada perquè falta la contrasenya inicial.");
            return "usuaris.contrasenya.obligatoria";
        }

        if (usuariDto.getRol() == Rol.ADMIN) {
            if (idActual == null) {
                if (usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank()) {
                    LOGGER.warn("Validació d'usuari ADMIN rebutjada perquè falta contrasenya.");
                    return "usuaris.admin.contrasenya.obligatoria";
                }
            } else {
                Optional<Usuari> usuariExistent = usuariRepository.findById(idActual);
                if (usuariExistent.isPresent()) {
                    String contrasenyaActual = usuariExistent.get().getContrasenya();
                    boolean novaContrasenyaBuida = usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank();
                    boolean contrasenyaActualBuida = contrasenyaActual == null || contrasenyaActual.isBlank();

                    if (novaContrasenyaBuida && contrasenyaActualBuida) {
                        LOGGER.warn("Validació d'usuari ADMIN rebutjada perquè falta contrasenya.");
                        return "usuaris.admin.contrasenya.obligatoria";
                    }
                }
            }
        }

        return null;
    }

    /**
     * Executa l'operació `actualitzarPerfil`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param perfilUsuariDto paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Usuari actualitzarPerfil(Long id, PerfilUsuariDto perfilUsuariDto) {
        Optional<Usuari> usuariExistent = usuariRepository.findById(id);
        if (usuariExistent.isEmpty()) {
            LOGGER.warn("No s'ha pogut actualitzar el perfil perquè l'usuari no existeix.");
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

        try {
            Usuari usuariDesat = usuariRepository.save(usuari);
            LOGGER.info("Perfil d'usuari actualitzat correctament amb id {}.", usuariDesat.getId());
            return usuariDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en actualitzar el perfil d'usuari amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `validarFotoPerfil`.
     *
     * @param fotoPerfil paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public String validarFotoPerfil(MultipartFile fotoPerfil) {
        if (fotoPerfil == null || fotoPerfil.isEmpty()) {
            return null;
        }

        String contentType = fotoPerfil.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            LOGGER.warn("Foto de perfil rebutjada per tipus de contingut no vàlid.");
            return "perfil.foto.format.invalid";
        }

        String extensio = obtenirExtensioFoto(fotoPerfil.getOriginalFilename());
        if (!EXTENSIONS_FOTO_PERMESES.contains(extensio)) {
            LOGGER.warn("Foto de perfil rebutjada per extensió no vàlida.");
            return "perfil.foto.format.invalid";
        }

        return null;
    }

    /**
     * Executa l'operació `actualitzarFotoPerfil`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param fotoPerfil paràmetre necessari per a l'operació.
     */
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
            LOGGER.info("Foto de perfil actualitzada per a l'usuari amb id {}.", id);
        } catch (IOException ex) {
            LOGGER.error("Error en desar la foto de perfil de l'usuari amb id {}.", id, ex);
            throw new IllegalStateException("perfil.foto.error.guardar", ex);
        }
    }

    /**
     * Executa l'operació `carregarFotoPerfil`.
     *
     * @param nomFitxer paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `validarCanviContrasenyaPerfil`.
     *
     * @param perfilUsuariDto paràmetre necessari per a l'operació.
     * @param usuariActual paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `hiHaCanviContrasenyaPerfil`.
     *
     * @param perfilUsuariDto paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private boolean hiHaCanviContrasenyaPerfil(PerfilUsuariDto perfilUsuariDto) {
        return !esBuit(perfilUsuariDto.getContrasenyaActual())
                || !esBuit(perfilUsuariDto.getNovaContrasenya())
                || !esBuit(perfilUsuariDto.getConfirmarContrasenya());
    }

    /**
     * Executa l'operació `esBuit`.
     *
     * @param valor paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private boolean esBuit(String valor) {
        return valor == null || valor.isBlank();
    }

    /**
     * Executa l'operació `convertirEntityAPerfilDto`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `convertirDtoAEntity`.
     *
     * @param usuariDto paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `convertirEntityADto`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `codificarContrasenyaSiCal`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     */
    private void codificarContrasenyaSiCal(Usuari usuari) {
        if (usuari.getContrasenya() != null && !usuari.getContrasenya().isBlank()) {
            usuari.setContrasenya(passwordEncoder.encode(usuari.getContrasenya()));
        }
    }

    /**
     * Executa l'operació `obtenirExtensioFoto`.
     *
     * @param nomOriginal paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String obtenirExtensioFoto(String nomOriginal) {
        if (nomOriginal == null || !nomOriginal.contains(".")) {
            return "jpg";
        }

        return nomOriginal.substring(nomOriginal.lastIndexOf('.') + 1)
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Executa l'operació `normalitzarDocument`.
     *
     * @param document paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarDocument(String document) {
        return IdentificadorFiscalValidator.normalitzar(document);
    }

    /**
     * Executa l'operació `normalitzarEmail`.
     *
     * @param email paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /**
     * Executa l'operació `normalitzarText`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarText(String text) {
        return text == null ? null : text.trim();
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
