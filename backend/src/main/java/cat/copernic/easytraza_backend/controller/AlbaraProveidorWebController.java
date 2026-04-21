package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.AlbaraProveidorDto;
import cat.copernic.easytraza_backend.dto.LotProveidorDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import cat.copernic.easytraza_backend.service.AlbaraProveidorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
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
    private ProveidorRepository proveidorRepository;

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistar(@RequestParam(required = false) String proveidorCif,
            @RequestParam(required = false) LocalDate dataRecepcio,
            Model model) {
        model.addAttribute("albarans", albaraProveidorService.buscar(proveidorCif, dataRecepcio));
        model.addAttribute("proveidorCif", proveidorCif);
        model.addAttribute("dataRecepcio", dataRecepcio);
        model.addAttribute("currentPath", "/web/albarans-proveidor");
        return "albarans-proveidor/llistar-albarans-proveidor";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrear(Model model) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();
        dto.setDataRecepcio(LocalDate.now());
        dto.getLots().add(new LotProveidorDto());

        carregarDadesFormulari(model);
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
            carregarDadesFormulari(model);
            return "albarans-proveidor/crear-albara-proveidor";
        }

        String errorNegoci = albaraProveidorService.validarAlbara(dto, null);
        if (errorNegoci != null) {
            carregarDadesFormulari(model);
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
            carregarDadesFormulari(model);
            model.addAttribute("albara", albaraProveidorService.convertirEntityADto(albara.get()));
            return "albarans-proveidor/editar-albara-proveidor";
        }

        redirectAttributes.addFlashAttribute(
                "missatgeError",
                messageSource.getMessage("albara.proveidor.flash.no.trobat", null, locale)
        );
        return "redirect:/web/albarans-proveidor";
    }

    @PostMapping("/actualitzar/{id}")
    public String actualitzar(@PathVariable Long id,
            @Valid @ModelAttribute("albara") AlbaraProveidorDto dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            carregarDadesFormulari(model);
            return "albarans-proveidor/editar-albara-proveidor";
        }

        String errorNegoci = albaraProveidorService.validarAlbara(dto, id);
        if (errorNegoci != null) {
            carregarDadesFormulari(model);
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "albarans-proveidor/editar-albara-proveidor";
        }

        AlbaraProveidor entity = albaraProveidorService.convertirDtoAEntity(dto);
        albaraProveidorService.update(id, entity);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("albara.proveidor.flash.actualitzat", null, locale)
        );

        return "redirect:/web/albarans-proveidor";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            albaraProveidorService.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("albara.proveidor.flash.eliminat", null, locale)
            );
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.proveidor.error.eliminar.relacions", null, locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.proveidor.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/albarans-proveidor";
    }

    private void carregarDadesFormulari(Model model) {
        model.addAttribute("proveidors", proveidorRepository.findAll());
        model.addAttribute("materiesPrimeres", materiaPrimaRepository.findAll());
    }
}
