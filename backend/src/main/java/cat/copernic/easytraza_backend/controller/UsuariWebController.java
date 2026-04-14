package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.service.UsuariService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/usuaris")
public class UsuariWebController {

    @Autowired
    private UsuariService usuariService;

    @GetMapping
    public String llistarUsuaris(Model model) {
        model.addAttribute("usuaris", usuariService.findAll());
        return "usuaris/Llistar-usuaris";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrearUsuari(Model model) {
        model.addAttribute("usuari", new Usuari());
        return "usuaris/crear-usuaris";
    }

    @PostMapping("/guardar")
    public String guardarUsuari(@ModelAttribute Usuari usuari) {
        usuariService.save(usuari);
        return "redirect:/web/usuaris";
    }
}
