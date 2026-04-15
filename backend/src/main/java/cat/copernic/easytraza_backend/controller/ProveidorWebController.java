package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.ProveidorDto;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.service.ProveidorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/web/proveidors")
public class ProveidorWebController {

    @Autowired
    private ProveidorService proveidorService;

    @GetMapping
    public String llistarProveidors(Model model) {
        model.addAttribute("proveidors", proveidorService.findAll());
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
            Model model) {

        if (result.hasErrors()) {
            return "proveidors/crear-proveidors";
        }

        String errorNegoci = proveidorService.validarProveidor(proveidorDto, null);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", errorNegoci);
            return "proveidors/crear-proveidors";
        }

        Proveidor proveidor = proveidorService.convertirDtoAEntity(proveidorDto);
        proveidorService.save(proveidor);

        return "redirect:/web/proveidors";
    }

    @GetMapping("/editar/{cif}")
    public String mostrarFormulariEditarProveidor(@PathVariable String cif, Model model) {
        Optional<Proveidor> proveidor = proveidorService.findById(cif);

        if (proveidor.isPresent()) {
            model.addAttribute("proveidor", proveidorService.convertirEntityADto(proveidor.get()));
            return "proveidors/editar-proveidors";
        } else {
            return "redirect:/web/proveidors";
        }
    }

    @PostMapping("/actualitzar/{cif}")
    public String actualitzarProveidor(@PathVariable String cif,
            @Valid @ModelAttribute("proveidor") ProveidorDto proveidorDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "proveidors/editar-proveidors";
        }

        String errorNegoci = proveidorService.validarProveidor(proveidorDto, cif);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", errorNegoci);
            return "proveidors/editar-proveidors";
        }

        Proveidor proveidor = proveidorService.convertirDtoAEntity(proveidorDto);
        proveidorService.update(cif, proveidor);

        return "redirect:/web/proveidors";
    }

    @GetMapping("/eliminar/{cif}")
    public String eliminarProveidor(@PathVariable String cif) {
        proveidorService.deleteById(cif);
        return "redirect:/web/proveidors";
    }
}
