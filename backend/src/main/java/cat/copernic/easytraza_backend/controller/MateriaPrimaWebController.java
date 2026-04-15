package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.MateriaPrimaDto;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.service.MateriaPrimaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/web/materies-primeres")
public class MateriaPrimaWebController {

    @Autowired
    private MateriaPrimaService materiaPrimaService;

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
            Model model) {

        if (result.hasErrors()) {
            return "materiesprimeres/crear-materies-primeres";
        }

        String errorNegoci = materiaPrimaService.validarMateriaPrima(materiaPrimaDto, null);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", errorNegoci);
            return "materiesprimeres/crear-materies-primeres";
        }

        MateriaPrima materiaPrima = materiaPrimaService.convertirDtoAEntity(materiaPrimaDto);
        materiaPrimaService.save(materiaPrima);

        return "redirect:/web/materies-primeres";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditarMateriaPrima(@PathVariable Long id, Model model) {
        Optional<MateriaPrima> materiaPrima = materiaPrimaService.findById(id);

        if (materiaPrima.isPresent()) {
            model.addAttribute("materiaPrima", materiaPrimaService.convertirEntityADto(materiaPrima.get()));
            return "materiesprimeres/editar-materies-primeres";
        } else {
            return "redirect:/web/materies-primeres";
        }
    }

    @PostMapping("/actualitzar/{id}")
    public String actualitzarMateriaPrima(@PathVariable Long id,
            @Valid @ModelAttribute("materiaPrima") MateriaPrimaDto materiaPrimaDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "materiesprimeres/editar-materies-primeres";
        }

        String errorNegoci = materiaPrimaService.validarMateriaPrima(materiaPrimaDto, id);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", errorNegoci);
            return "materiesprimeres/editar-materies-primeres";
        }

        MateriaPrima materiaPrima = materiaPrimaService.convertirDtoAEntity(materiaPrimaDto);
        materiaPrimaService.update(id, materiaPrima);

        return "redirect:/web/materies-primeres";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMateriaPrima(@PathVariable Long id) {
        materiaPrimaService.deleteById(id);
        return "redirect:/web/materies-primeres";
    }
}
