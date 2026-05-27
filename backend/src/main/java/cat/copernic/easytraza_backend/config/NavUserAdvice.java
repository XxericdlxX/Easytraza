package cat.copernic.easytraza_backend.config;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Configuració `NavUserAdvice` del projecte EasyTraza.
 */
@ControllerAdvice
public class NavUserAdvice {

    @Autowired
    private UsuariRepository usuariRepository;

    /**
     * Executa l'operació `navUserName`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @ModelAttribute("navUserName")
    public String navUserName() {
        return obtenirUsuariAutenticat()
                .map(this::construirNomVisible)
                .orElseGet(this::obtenirPrincipalVisible);
    }

    /**
     * Executa l'operació `navUserInitials`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @ModelAttribute("navUserInitials")
    public String navUserInitials() {
        return obtenirInicials(navUserName());
    }

    /**
     * Executa l'operació `navUserPhoto`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @ModelAttribute("navUserPhoto")
    public String navUserPhoto() {
        return obtenirUsuariAutenticat()
                .map(Usuari::getFotoPerfilNom)
                .filter(foto -> foto != null && !foto.isBlank())
                .orElse("");
    }

    /**
     * Executa l'operació `navUserEmail`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @ModelAttribute("navUserEmail")
    public String navUserEmail() {
        return obtenirUsuariAutenticat()
                .map(Usuari::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .orElseGet(this::obtenirPrincipalVisible);
    }

    /**
     * Executa l'operació `navUserRole`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @ModelAttribute("navUserRole")
    public String navUserRole() {
        return obtenirUsuariAutenticat()
                .map(Usuari::getRol)
                .map(Enum::name)
                .orElseGet(() -> esAdminAutenticat() ? "ADMIN" : "");
    }

    /**
     * Executa l'operació `navUserAuthenticated`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @ModelAttribute("navUserAuthenticated")
    public boolean navUserAuthenticated() {
        return obtenirAutenticacioValida().isPresent();
    }

    /**
     * Executa l'operació `navUserAdmin`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @ModelAttribute("navUserAdmin")
    public boolean navUserAdmin() {
        return esAdminAutenticat();
    }

    /**
     * Executa l'operació `obtenirUsuariAutenticat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    private Optional<Usuari> obtenirUsuariAutenticat() {
        return obtenirAutenticacioValida()
                .map(Authentication::getName)
                .filter(principalName -> principalName != null && !principalName.isBlank())
                .flatMap(usuariRepository::findByEmailIgnoreCase);
    }

    /**
     * Executa l'operació `obtenirAutenticacioValida`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    private Optional<Authentication> obtenirAutenticacioValida() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        return Optional.of(authentication);
    }

    /**
     * Executa l'operació `esAdminAutenticat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    private boolean esAdminAutenticat() {
        return obtenirAutenticacioValida()
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(authorities -> authorities.stream())
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    /**
     * Executa l'operació `obtenirPrincipalVisible`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    private String obtenirPrincipalVisible() {
        return obtenirAutenticacioValida()
                .map(Authentication::getName)
                .filter(principalName -> principalName != null && !principalName.isBlank())
                .orElse("");
    }

    /**
     * Executa l'operació `construirNomVisible`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String construirNomVisible(Usuari usuari) {
        String nom = usuari.getNom() != null ? usuari.getNom().trim() : "";
        String cognoms = usuari.getCognoms() != null ? usuari.getCognoms().trim() : "";
        String complet = (nom + " " + cognoms).trim();

        if (!complet.isBlank()) {
            return complet;
        }

        return usuari.getEmail() != null ? usuari.getEmail() : "";
    }

    /**
     * Executa l'operació `obtenirInicials`.
     *
     * @param nomVisible paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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
