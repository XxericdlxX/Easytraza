package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.ProducteDto;
import cat.copernic.easytraza_backend.model.Producte;
import cat.copernic.easytraza_backend.service.LotProveidorService;
import cat.copernic.easytraza_backend.service.ProducteService;
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
@RequestMapping("/web/productes")
public class ProducteWebController {

    @Autowired
    private ProducteService producteService;

    @Autowired
    private LotProveidorService lotProveidorService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistarProductes(@RequestParam(required = false) String nom,
            @RequestParam(required = false) String descripcio,
            Model model) {

        model.addAttribute("productes", producteService.buscar(nom, descripcio));
        model.addAttribute("nom", nom);
        model.addAttribute("descripcio", descripcio);
        model.addAttribute("currentPath", "/web/productes");

        return "productes/llistar-productes";
    }

    @GetMapping("/{id}/produccio-lots")
    public String veureProduccioLotsProducte(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<Producte> producte = producteService.findById(id);

        if (producte.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("productes.flash.no.trobat", null, locale)
            );
            return "redirect:/web/productes";
        }

        model.addAttribute("producte", producte.get());
        model.addAttribute("liniesProduccio", producteService.cercarProduccioLotsPerProducte(id));
        model.addAttribute("currentPath", "/web/productes");

        return "productes/produccio-lots-producte";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrearProducte(Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (!lotProveidorService.existeixLotObert()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("productes.error.crear.sense.lots.oberts", null, locale)
            );
            return "redirect:/web/productes";
        }

        model.addAttribute("producte", new ProducteDto());
        return "productes/crear-productes";
    }

    @PostMapping("/guardar")
    public String guardarProducte(@Valid @ModelAttribute("producte") ProducteDto producteDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (!lotProveidorService.existeixLotObert()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("productes.error.crear.sense.lots.oberts", null, locale)
            );
            return "redirect:/web/productes";
        }

        if (result.hasErrors()) {
            return "productes/crear-productes";
        }

        String errorNegoci = producteService.validarProducte(producteDto, null);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "productes/crear-productes";
        }

        Producte producte = producteService.convertirDtoAEntity(producteDto);
        producteService.save(producte);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("productes.flash.creat", null, locale)
        );

        return "redirect:/web/productes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditarProducte(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<Producte> producte = producteService.findById(id);

        if (producte.isPresent()) {
            model.addAttribute("producte", producteService.convertirEntityADto(producte.get()));
            return "productes/editar-productes";
        } else {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("productes.flash.no.trobat", null, locale)
            );
            return "redirect:/web/productes";
        }
    }

    @PostMapping("/actualitzar/{id}")
    public String actualitzarProducte(@PathVariable Long id,
            @Valid @ModelAttribute("producte") ProducteDto producteDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            return "productes/editar-productes";
        }

        String errorNegoci = producteService.validarProducte(producteDto, id);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "productes/editar-productes";
        }

        Producte producte = producteService.convertirDtoAEntity(producteDto);
        producteService.update(id, producte);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("productes.flash.actualitzat", null, locale)
        );

        return "redirect:/web/productes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducte(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            producteService.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("productes.flash.eliminat", null, locale)
            );
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("productes.error.eliminar.relacions", null, locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("productes.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/productes";
    }
}
