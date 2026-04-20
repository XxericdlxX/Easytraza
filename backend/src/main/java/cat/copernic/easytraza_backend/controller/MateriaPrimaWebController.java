package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.MateriaPrimaDto;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.service.MateriaPrimaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/web/materies-primeres")
public class MateriaPrimaWebController {

    @Autowired
    private MateriaPrimaService materiaPrimaService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistarMateriesPrimeres(Model model) {
        model.addAttribute("materiesPrimeres", materiaPrimaService.findAll());
        model.addAttribute("currentPath", "/web/materies-primeres");
        return "materiesprimeres/llistar-materies-primeres";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrearMateriaPrima(Model model) {
        model.addAttribute("materiaPrima", new MateriaPrimaDto());
        return "materiesprimeres/crear-materies-primeres";
    }

    @PostMapping("/guardar")
    public String guardarMateriaPrima(@Valid @ModelAttribute("materiaPrima") MateriaPrimaDto materiaPrimaDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            return "materiesprimeres/crear-materies-primeres";
        }

        String errorNegoci = materiaPrimaService.validarMateriaPrima(materiaPrimaDto, null);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "materiesprimeres/crear-materies-primeres";
        }

        MateriaPrima materiaPrima = materiaPrimaService.convertirDtoAEntity(materiaPrimaDto);
        materiaPrimaService.save(materiaPrima);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("materies.flash.creada", null, locale)
        );

        return "redirect:/web/materies-primeres";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditarMateriaPrima(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        Optional<MateriaPrima> materiaPrima = materiaPrimaService.findById(id);

        if (materiaPrima.isPresent()) {
            model.addAttribute("materiaPrima", materiaPrimaService.convertirEntityADto(materiaPrima.get()));
            return "materiesprimeres/editar-materies-primeres";
        } else {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("materies.flash.no.trobada", null, locale)
            );
            return "redirect:/web/materies-primeres";
        }
    }

    @PostMapping("/actualitzar/{id}")
    public String actualitzarMateriaPrima(@PathVariable Long id,
            @Valid @ModelAttribute("materiaPrima") MateriaPrimaDto materiaPrimaDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            return "materiesprimeres/editar-materies-primeres";
        }

        String errorNegoci = materiaPrimaService.validarMateriaPrima(materiaPrimaDto, id);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "materiesprimeres/editar-materies-primeres";
        }

        MateriaPrima materiaPrima = materiaPrimaService.convertirDtoAEntity(materiaPrimaDto);
        materiaPrimaService.update(id, materiaPrima);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("materies.flash.actualitzada", null, locale)
        );

        return "redirect:/web/materies-primeres";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMateriaPrima(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            materiaPrimaService.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("materies.flash.eliminada", null, locale)
            );
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("materies.error.eliminar.relacions", null, locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("materies.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/materies-primeres";
    }
}
