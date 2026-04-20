package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.UsuariDto;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.service.UsuariService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/usuaris")
public class UsuariWebController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistarUsuaris(Model model) {
        model.addAttribute("usuaris", usuariService.findAll());
        model.addAttribute("currentPath", "/web/usuaris");
        return "usuaris/llistar-usuaris";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrearUsuari(Model model) {
        model.addAttribute("usuari", new UsuariDto());
        return "usuaris/crear-usuaris";
    }

    @PostMapping("/guardar")
    public String guardarUsuari(@Valid @ModelAttribute("usuari") UsuariDto usuariDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            return "usuaris/crear-usuaris";
        }

        String errorNegoci = usuariService.validarUsuari(usuariDto, null);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "usuaris/crear-usuaris";
        }

        Usuari usuari = usuariService.convertirDtoAEntity(usuariDto);
        usuariService.save(usuari);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("usuaris.flash.creat", null, locale)
        );

        return "redirect:/web/usuaris";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditarUsuari(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        Optional<Usuari> usuari = usuariService.findById(id);

        if (usuari.isPresent()) {
            Usuari usuariEntity = usuari.get();
            model.addAttribute("usuari", usuariService.convertirEntityADto(usuariEntity));
            model.addAttribute("superadminProtegit", usuariService.isProtectedUser(usuariEntity));
            return "usuaris/editar-usuaris";
        } else {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("usuaris.flash.no.trobat", null, locale)
            );
            return "redirect:/web/usuaris";
        }
    }

    @PostMapping("/actualitzar/{id}")
    public String actualitzarUsuari(@PathVariable Long id,
            @Valid @ModelAttribute("usuari") UsuariDto usuariDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<Usuari> usuariExistent = usuariService.findById(id);
        boolean superadminProtegit = usuariExistent.map(usuariService::isProtectedUser).orElse(false);
        model.addAttribute("superadminProtegit", superadminProtegit);

        if (result.hasErrors()) {
            return "usuaris/editar-usuaris";
        }

        String errorNegoci = usuariService.validarUsuari(usuariDto, id);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "usuaris/editar-usuaris";
        }

        Usuari usuari = usuariService.convertirDtoAEntity(usuariDto);
        usuariService.update(id, usuari);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("usuaris.flash.actualitzat", null, locale)
        );

        return "redirect:/web/usuaris";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuari(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            if (usuariService.isProtectedUserById(id)) {
                redirectAttributes.addFlashAttribute(
                        "missatgeError",
                        messageSource.getMessage("usuaris.error.eliminar.superadmin", null, locale)
                );
                return "redirect:/web/usuaris";
            }

            boolean eliminat = usuariService.deleteById(id);

            if (!eliminat) {
                redirectAttributes.addFlashAttribute(
                        "missatgeError",
                        messageSource.getMessage("usuaris.error.eliminar.superadmin", null, locale)
                );
                return "redirect:/web/usuaris";
            }

            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("usuaris.flash.eliminat", null, locale)
            );
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("usuaris.error.eliminar.relacions", null, locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("usuaris.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/usuaris";
    }
}
