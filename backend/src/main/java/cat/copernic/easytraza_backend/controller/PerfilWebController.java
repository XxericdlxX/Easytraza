package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.PerfilUsuariDto;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.service.UsuariService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/perfil")
public class PerfilWebController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String mostrarPerfil(Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<Usuari> usuariAutenticat = obtenirUsuariAutenticat();
        if (usuariAutenticat.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("perfil.error.no.trobat", null, locale)
            );
            return "redirect:/";
        }

        Usuari usuari = usuariAutenticat.get();
        model.addAttribute("perfil", usuariService.convertirEntityAPerfilDto(usuari));
        model.addAttribute("perfilEmailBloquejat", usuariService.isProtectedUser(usuari));
        model.addAttribute("currentPath", "/web/perfil");
        return "perfil/editar-perfil";
    }

    @PostMapping("/actualitzar")
    public String actualitzarPerfil(@Valid @ModelAttribute("perfil") PerfilUsuariDto perfilUsuariDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<Usuari> usuariAutenticat = obtenirUsuariAutenticat();
        if (usuariAutenticat.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("perfil.error.no.trobat", null, locale)
            );
            return "redirect:/";
        }

        Usuari usuariActual = usuariAutenticat.get();
        model.addAttribute("perfilEmailBloquejat", usuariService.isProtectedUser(usuariActual));
        model.addAttribute("currentPath", "/web/perfil");

        if (result.hasErrors()) {
            return "perfil/editar-perfil";
        }

        String errorNegoci = usuariService.validarPerfilUsuari(perfilUsuariDto, usuariActual.getId());
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "perfil/editar-perfil";
        }

        String emailAnterior = usuariActual.getEmail();
        Usuari usuariActualitzat = usuariService.actualitzarPerfil(usuariActual.getId(), perfilUsuariDto);

        if (usuariActualitzat == null) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("perfil.error.no.trobat", null, locale)
            );
            return "redirect:/";
        }

        actualitzarPrincipalSiCanviaEmail(emailAnterior, usuariActualitzat.getEmail());

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("perfil.flash.actualitzat", null, locale)
        );

        return "redirect:/web/perfil";
    }

    private Optional<Usuari> obtenirUsuariAutenticat() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        return usuariService.findByEmailIgnoreCase(email);
    }

    private void actualitzarPrincipalSiCanviaEmail(String emailAnterior, String emailNou) {
        if (emailAnterior == null || emailNou == null || emailAnterior.equalsIgnoreCase(emailNou)) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return;
        }

        UsernamePasswordAuthenticationToken authenticationActualitzada =
                UsernamePasswordAuthenticationToken.authenticated(
                        emailNou,
                        authentication.getCredentials(),
                        authentication.getAuthorities()
                );
        authenticationActualitzada.setDetails(authentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(authenticationActualitzada);
    }
}
