package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.ProveidorDto;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.service.ProveidorService;
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
@RequestMapping("/web/proveidors")
public class ProveidorWebController {

    @Autowired
    private ProveidorService proveidorService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistarProveidors(Model model) {
        model.addAttribute("proveidors", proveidorService.findAll());
        model.addAttribute("currentPath", "/web/proveidors");
        return "proveidors/llistar-proveidors";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrearProveidor(Model model) {
        model.addAttribute("proveidor", new ProveidorDto());
        return "proveidors/crear-proveidors";
    }

    @PostMapping("/guardar")
    public String guardarProveidor(@Valid @ModelAttribute("proveidor") ProveidorDto proveidorDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            return "proveidors/crear-proveidors";
        }

        String errorNegoci = proveidorService.validarProveidor(proveidorDto, null);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "proveidors/crear-proveidors";
        }

        Proveidor proveidor = proveidorService.convertirDtoAEntity(proveidorDto);
        proveidorService.save(proveidor);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("proveidors.flash.creat", null, locale)
        );

        return "redirect:/web/proveidors";
    }

    @GetMapping("/editar/{cif}")
    public String mostrarFormulariEditarProveidor(@PathVariable String cif, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        Optional<Proveidor> proveidor = proveidorService.findById(cif);

        if (proveidor.isPresent()) {
            model.addAttribute("proveidor", proveidorService.convertirEntityADto(proveidor.get()));
            return "proveidors/editar-proveidors";
        } else {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("proveidors.flash.no.trobat", null, locale)
            );
            return "redirect:/web/proveidors";
        }
    }

    @PostMapping("/actualitzar/{cif}")
    public String actualitzarProveidor(@PathVariable String cif,
            @Valid @ModelAttribute("proveidor") ProveidorDto proveidorDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            return "proveidors/editar-proveidors";
        }

        String errorNegoci = proveidorService.validarProveidor(proveidorDto, cif);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "proveidors/editar-proveidors";
        }

        Proveidor proveidor = proveidorService.convertirDtoAEntity(proveidorDto);
        proveidorService.update(cif, proveidor);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("proveidors.flash.actualitzat", null, locale)
        );

        return "redirect:/web/proveidors";
    }

    @GetMapping("/eliminar/{cif}")
    public String eliminarProveidor(@PathVariable String cif, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            proveidorService.deleteById(cif);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("proveidors.flash.eliminat", null, locale)
            );
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("proveidors.error.eliminar.relacions", null, locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("proveidors.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/proveidors";
    }
}
