package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.AlbaraProveidorDto;
import cat.copernic.easytraza_backend.dto.LotProveidorDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.service.AlbaraProveidorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/web/albarans-proveidor")
public class AlbaraProveidorWebController {

    @Autowired
    private AlbaraProveidorService albaraProveidorService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistar(Model model) {
        model.addAttribute("albarans", albaraProveidorService.findAll());
        model.addAttribute("currentPath", "/web/albarans-proveidor");
        return "albarans-proveidor/llistar-albarans-proveidor";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrear(Model model) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();
        dto.setDataRecepcio(LocalDate.now());
        dto.getLots().add(new LotProveidorDto());
        model.addAttribute("albara", dto);
        return "albarans-proveidor/crear-albara-proveidor";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("albara") AlbaraProveidorDto dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            return "albarans-proveidor/crear-albara-proveidor";
        }

        String errorNegoci = albaraProveidorService.validarAlbara(dto);
        if (errorNegoci != null) {
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "albarans-proveidor/crear-albara-proveidor";
        }

        AlbaraProveidor entity = albaraProveidorService.convertirDtoAEntity(dto);
        albaraProveidorService.save(entity);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("albara.proveidor.flash.creat", null, locale)
        );

        return "redirect:/web/albarans-proveidor";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditar(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        Optional<AlbaraProveidor> albara = albaraProveidorService.findById(id);

        if (albara.isPresent()) {
            model.addAttribute("albara", albaraProveidorService.convertirEntityADto(albara.get()));
            return "albarans-proveidor/editar-albara-proveidor";
        }

        redirectAttributes.addFlashAttribute(
                "missatgeError",
                messageSource.getMessage("albara.proveidor.flash.no.trobat", null, locale)
        );
        return "redirect:/web/albarans-proveidor";
    }
}
