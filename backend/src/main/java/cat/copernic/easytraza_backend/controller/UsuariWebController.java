package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.UsuariDto;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.service.UsuariService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/web/usuaris")
public class UsuariWebController {

    @Autowired
    private UsuariService usuariService;

    @GetMapping
    public String llistarUsuaris(Model model) {
        model.addAttribute("usuaris", usuariService.findAll());
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
            Model model) {

        if (result.hasErrors()) {
            return "usuaris/crear-usuaris";
        }

        String errorNegoci = usuariService.validarUsuari(usuariDto, null);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", errorNegoci);
            return "usuaris/crear-usuaris";
        }

        Usuari usuari = usuariService.convertirDtoAEntity(usuariDto);
        usuariService.save(usuari);

        return "redirect:/web/usuaris";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditarUsuari(@PathVariable Long id, Model model) {
        Optional<Usuari> usuari = usuariService.findById(id);

        if (usuari.isPresent()) {
            model.addAttribute("usuari", usuariService.convertirEntityADto(usuari.get()));
            return "usuaris/editar-usuaris";
        } else {
            return "redirect:/web/usuaris";
        }
    }

    @PostMapping("/actualitzar/{id}")
    public String actualitzarUsuari(@PathVariable Long id,
            @Valid @ModelAttribute("usuari") UsuariDto usuariDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "usuaris/editar-usuaris";
        }

        String errorNegoci = usuariService.validarUsuari(usuariDto, id);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", errorNegoci);
            return "usuaris/editar-usuaris";
        }

        Usuari usuari = usuariService.convertirDtoAEntity(usuariDto);
        usuariService.update(id, usuari);

        return "redirect:/web/usuaris";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuari(@PathVariable Long id) {
        usuariService.deleteById(id);
        return "redirect:/web/usuaris";
    }
}
