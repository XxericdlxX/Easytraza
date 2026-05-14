package cat.copernic.easytraza_backend.security;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.Rol;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavUserAdvice {

    @Autowired
    private UsuariRepository usuariRepository;

    @ModelAttribute("navUserName")
    public String navUserName() {
        return obtenirUsuariAutenticat()
                .map(this::construirNomVisible)
                .orElse("");
    }

    @ModelAttribute("navUserInitials")
    public String navUserInitials() {
        return obtenirInicials(navUserName());
    }

    @ModelAttribute("navUserEmail")
    public String navUserEmail() {
        return obtenirUsuariAutenticat()
                .map(Usuari::getEmail)
                .orElse("");
    }

    @ModelAttribute("navUserRole")
    public String navUserRole() {
        return obtenirUsuariAutenticat()
                .map(Usuari::getRol)
                .map(Enum::name)
                .orElse("");
    }

    @ModelAttribute("navUserAuthenticated")
    public boolean navUserAuthenticated() {
        return obtenirUsuariAutenticat().isPresent();
    }

    @ModelAttribute("navUserAdmin")
    public boolean navUserAdmin() {
        return obtenirUsuariAutenticat()
                .map(Usuari::getRol)
                .map(rol -> rol == Rol.ADMIN)
                .orElse(false);
    }

    private Optional<Usuari> obtenirUsuariAutenticat() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        String principalName = authentication.getName();
        if (principalName == null || principalName.isBlank()) {
            return Optional.empty();
        }

        return usuariRepository.findByEmailIgnoreCase(principalName);
    }

    private String construirNomVisible(Usuari usuari) {
        String nom = usuari.getNom() != null ? usuari.getNom().trim() : "";
        String cognoms = usuari.getCognoms() != null ? usuari.getCognoms().trim() : "";
        String complet = (nom + " " + cognoms).trim();

        if (!complet.isBlank()) {
            return complet;
        }

        return usuari.getEmail() != null ? usuari.getEmail() : "";
    }

    private String obtenirInicials(String nomVisible) {
        if (nomVisible == null || nomVisible.isBlank()) {
            return "ET";
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
