package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.PerfilUsuariDto;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.service.UsuariService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador `PerfilWebController` del projecte EasyTraza.
 */
@Controller
@RequestMapping("/web/perfil")
public class PerfilWebController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Executa l'operació `mostrarPerfil`.
     *
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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
        boolean perfilProtegit = usuariService.isProtectedUser(usuari);
        model.addAttribute("perfil", usuariService.convertirEntityAPerfilDto(usuari));
        model.addAttribute("perfilEmailBloquejat", perfilProtegit);
        model.addAttribute("perfilDadesBloquejades", perfilProtegit);
        model.addAttribute("currentPath", "/web/perfil");
        return "perfil/editar-perfil";
    }

    /**
     * Executa l'operació `actualitzarPerfil`.
     *
     * @param perfilUsuariDto paràmetre necessari per a l'operació.
     * @param result paràmetre necessari per a l'operació.
     * @param fotoPerfil paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/actualitzar")
    public String actualitzarPerfil(@Valid @ModelAttribute("perfil") PerfilUsuariDto perfilUsuariDto,
            BindingResult result,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
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
        boolean perfilProtegit = usuariService.isProtectedUser(usuariActual);
        model.addAttribute("perfilEmailBloquejat", perfilProtegit);
        model.addAttribute("perfilDadesBloquejades", perfilProtegit);
        model.addAttribute("currentPath", "/web/perfil");

        if (result.hasErrors()) {
            return "perfil/editar-perfil";
        }

        String errorFoto = usuariService.validarFotoPerfil(fotoPerfil);
        if (errorFoto != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorFoto, null, locale));
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

        try {
            usuariService.actualitzarFotoPerfil(usuariActualitzat.getId(), fotoPerfil);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );
            return "redirect:/web/perfil";
        }

        actualitzarPrincipalSiCanviaEmail(emailAnterior, usuariActualitzat.getEmail());

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("perfil.flash.actualitzat", null, locale)
        );

        return "redirect:/web/perfil";
    }

    /**
     * Executa l'operació `mostrarFotoPerfil`.
     *
     * @param nomFitxer paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/foto/{nomFitxer}")
    public ResponseEntity<Resource> mostrarFotoPerfil(@PathVariable String nomFitxer) {
        Optional<Resource> foto = usuariService.carregarFotoPerfil(nomFitxer);
        if (foto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaType.IMAGE_JPEG;
        try {
            String contentType = Files.probeContentType(foto.get().getFile().toPath());
            if (contentType != null && !contentType.isBlank()) {
                mediaType = MediaType.parseMediaType(contentType);
            }
        } catch (IOException ex) {
            // Manté image/jpeg per defecte si no es pot detectar el tipus MIME.
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(foto.get());
    }

    /**
     * Executa l'operació `obtenirUsuariAutenticat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `actualitzarPrincipalSiCanviaEmail`.
     *
     * @param emailAnterior paràmetre necessari per a l'operació.
     * @param emailNou paràmetre necessari per a l'operació.
     */
    private void actualitzarPrincipalSiCanviaEmail(String emailAnterior, String emailNou) {
        if (emailAnterior == null || emailNou == null || emailAnterior.equalsIgnoreCase(emailNou)) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return;
        }

        UsernamePasswordAuthenticationToken authenticationActualitzada
                = UsernamePasswordAuthenticationToken.authenticated(
                        emailNou,
                        authentication.getCredentials(),
                        authentication.getAuthorities()
                );
        authenticationActualitzada.setDetails(authentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(authenticationActualitzada);
    }
}
