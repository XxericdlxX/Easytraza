package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.RestablirContrasenyaDto;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class RecuperacioContrasenyaService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.login");
    private static final int TOKEN_BYTES = 32;
    private static final int MINUTS_VALIDESA_TOKEN = 15;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder TOKEN_ENCODER = Base64.getUrlEncoder().withoutPadding();

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MessageSource messageSource;

    @Value("${spring.mail.username}")
    private String emailEmissor;

    public void sollicitarRecuperacio(String email, String baseUrl, Locale locale) {
        String emailNormalitzat = normalitzarEmail(email);
        Optional<Usuari> usuariOpt = usuariRepository.findByEmailIgnoreCase(emailNormalitzat);

        if (usuariOpt.isEmpty()) {
            LOGGER.info("Sol·licitud de recuperació ignorada perquè no existeix cap usuari per al correu indicat.");
            return;
        }

        Usuari usuari = usuariOpt.get();
        String token = generarTokenSegur();
        usuari.setTokenRecuperacioContrasenya(token);
        usuari.setTokenRecuperacioExpiracio(LocalDateTime.now().plusMinutes(MINUTS_VALIDESA_TOKEN));
        usuariRepository.save(usuari);

        String urlRestabliment = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/restablir-contrasenya")
                .queryParam("token", token)
                .queryParam("lang", locale.getLanguage())
                .build()
                .toUriString();

        try {
            enviarCorreuRecuperacio(usuari, urlRestabliment, locale);
        } catch (MailException ex) {
            usuari.setTokenRecuperacioContrasenya(null);
            usuari.setTokenRecuperacioExpiracio(null);
            usuariRepository.save(usuari);
            LOGGER.error("No s'ha pogut enviar el correu de recuperació de contrasenya per a l'usuari amb id {}", usuari.getId(), ex);
        }
    }

    public boolean tokenValid(String token) {
        return obtenirUsuariPerTokenValid(token).isPresent();
    }

    public String validarRestabliment(RestablirContrasenyaDto restablirContrasenyaDto) {
        if (!tokenValid(restablirContrasenyaDto.getToken())) {
            return "recuperacio.error.token.invalid";
        }

        String novaContrasenya = restablirContrasenyaDto.getNovaContrasenya();
        String confirmacio = restablirContrasenyaDto.getConfirmarContrasenya();

        if (novaContrasenya == null || confirmacio == null || !novaContrasenya.equals(confirmacio)) {
            return "recuperacio.error.contrasenyes.no.coincideixen";
        }

        return null;
    }

    public boolean restablirContrasenya(RestablirContrasenyaDto restablirContrasenyaDto) {
        Optional<Usuari> usuariOpt = obtenirUsuariPerTokenValid(restablirContrasenyaDto.getToken());
        if (usuariOpt.isEmpty()) {
            return false;
        }

        Usuari usuari = usuariOpt.get();
        usuari.setContrasenya(passwordEncoder.encode(restablirContrasenyaDto.getNovaContrasenya()));
        usuari.setTokenRecuperacioContrasenya(null);
        usuari.setTokenRecuperacioExpiracio(null);
        usuariRepository.save(usuari);
        return true;
    }

    private void enviarCorreuRecuperacio(Usuari usuari, String urlRestabliment, Locale locale) {
        String nomVisible = obtenirNomVisible(usuari);
        String assumpte = messageSource.getMessage("recuperacio.email.assumpte", null, locale);
        String cos = messageSource.getMessage(
                "recuperacio.email.cos",
                new Object[]{nomVisible, urlRestabliment, MINUTS_VALIDESA_TOKEN},
                locale
        );

        SimpleMailMessage missatge = new SimpleMailMessage();
        missatge.setFrom(emailEmissor);
        missatge.setTo(usuari.getEmail());
        missatge.setSubject(assumpte);
        missatge.setText(cos);
        javaMailSender.send(missatge);
    }

    private Optional<Usuari> obtenirUsuariPerTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        Optional<Usuari> usuariOpt = usuariRepository.findByTokenRecuperacioContrasenya(token.trim());
        if (usuariOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuari usuari = usuariOpt.get();
        LocalDateTime expiracio = usuari.getTokenRecuperacioExpiracio();
        if (expiracio == null || expiracio.isBefore(LocalDateTime.now())) {
            usuari.setTokenRecuperacioContrasenya(null);
            usuari.setTokenRecuperacioExpiracio(null);
            usuariRepository.save(usuari);
            return Optional.empty();
        }

        return Optional.of(usuari);
    }

    private String generarTokenSegur() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return TOKEN_ENCODER.encodeToString(bytes);
    }

    private String normalitzarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String obtenirNomVisible(Usuari usuari) {
        String nom = usuari.getNom() != null ? usuari.getNom().trim() : "";
        String cognoms = usuari.getCognoms() != null ? usuari.getCognoms().trim() : "";
        String nomComplet = (nom + " " + cognoms).trim();
        return nomComplet.isBlank() ? usuari.getEmail() : nomComplet;
    }
}
