package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.ProducteDto;
import cat.copernic.easytraza_backend.model.Producte;
import cat.copernic.easytraza_backend.service.ProducteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/web/productes")
public class ProducteWebController {

    @Autowired
    private ProducteService producteService;

    @GetMapping
    public String llistarProductes(@RequestParam(required = false) String cerca, Model model) {
        model.addAttribute("productes", producteService.buscarPerDescripcio(cerca));
        model.addAttribute("cerca", cerca);
        model.addAttribute("currentPath", "/web/productes");
        return "productes/llistar-productes";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrearProducte(Model model) {
        model.addAttribute("producte", new ProducteDto());
        return "productes/crear-productes";
    }

    @PostMapping("/guardar")
    public String guardarProducte(@Valid @ModelAttribute("producte") ProducteDto producteDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "productes/crear-productes";
        }

        String errorNegoci = producteService.validarProducte(producteDto, null);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", errorNegoci);
            return "productes/crear-productes";
        }

        Producte producte = producteService.convertirDtoAEntity(producteDto);
        producteService.save(producte);

        return "redirect:/web/productes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditarProducte(@PathVariable Long id, Model model) {
        Optional<Producte> producte = producteService.findById(id);

        if (producte.isPresent()) {
            model.addAttribute("producte", producteService.convertirEntityADto(producte.get()));
            return "productes/editar-productes";
        } else {
            return "redirect:/web/productes";
        }
    }

    @PostMapping("/actualitzar/{id}")
    public String actualitzarProducte(@PathVariable Long id,
            @Valid @ModelAttribute("producte") ProducteDto producteDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "productes/editar-productes";
        }

        String errorNegoci = producteService.validarProducte(producteDto, id);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", errorNegoci);
            return "productes/editar-productes";
        }

        Producte producte = producteService.convertirDtoAEntity(producteDto);
        producteService.update(id, producte);

        return "redirect:/web/productes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducte(@PathVariable Long id) {
        producteService.deleteById(id);
        return "redirect:/web/productes";
    }
}
