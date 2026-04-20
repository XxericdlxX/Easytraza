package cat.copernic.easytraza_backend.config;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class NavUserAdvice {

    private static final String SUPERADMIN_EMAIL = "superadmin@easytraza.local";

    @Autowired
    private UsuariRepository usuariRepository;

    @ModelAttribute("navUserName")
    public String navUserName() {
        return obtenirNomVisible();
    }

    @ModelAttribute("navUserInitials")
    public String navUserInitials() {
        return obtenirInicials(obtenirNomVisible());
    }

    private String obtenirNomVisible() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            String principalName = authentication.getName();

            Optional<Usuari> usuariAutenticat = usuariRepository.findByEmailIgnoreCase(principalName);
            if (usuariAutenticat.isPresent()) {
                Usuari usuari = usuariAutenticat.get();
                return construirNomVisible(usuari);
            }

            if (principalName != null && !principalName.isBlank()) {
                return principalName;
            }
        }

        Optional<Usuari> superadmin = usuariRepository.findByEmailIgnoreCase(SUPERADMIN_EMAIL);
        if (superadmin.isPresent()) {
            return construirNomVisible(superadmin.get());
        }

        return "Usuari web";
    }

    private String construirNomVisible(Usuari usuari) {
        String nom = usuari.getNom() != null ? usuari.getNom().trim() : "";
        String cognoms = usuari.getCognoms() != null ? usuari.getCognoms().trim() : "";
        String complet = (nom + " " + cognoms).trim();

        if (!complet.isBlank()) {
            return complet;
        }

        return usuari.getEmail() != null ? usuari.getEmail() : "Usuari web";
    }

    private String obtenirInicials(String nomVisible) {
        if (nomVisible == null || nomVisible.isBlank()) {
            return "UW";
        }

        String[] parts = nomVisible.trim().split("\\s+");

        if (parts.length == 1) {
            String paraula = parts[0];
            return paraula.substring(0, Math.min(2, paraula.length())).toUpperCase();
        }

        String primera = parts[0].substring(0, 1);
        String segona = parts[1].substring(0, 1);
        return (primera + segona).toUpperCase();
    }
}
