package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.model.enums.EstatLot;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import cat.copernic.easytraza_backend.service.LotProveidorService;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/lots")
public class LotWebController {

    @Autowired
    private LotProveidorService lotProveidorService;

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistarLots(
            @RequestParam(required = false) String codiLot,
            @RequestParam(required = false) EstatLot estat,
            @RequestParam(required = false) Long materiaPrimaId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataRecepcio,
            Model model) {

        model.addAttribute("lots", lotProveidorService.cercar(codiLot, estat, materiaPrimaId, dataRecepcio));
        model.addAttribute("materiesPrimeres", materiaPrimaRepository.findAll());
        model.addAttribute("estatsLot", EstatLot.values());

        model.addAttribute("codiLot", codiLot);
        model.addAttribute("estat", estat);
        model.addAttribute("materiaPrimaId", materiaPrimaId);
        model.addAttribute("dataRecepcio", dataRecepcio);

        model.addAttribute("currentPath", "/web/lots");

        return "lots/llistar-lots";
    }

    @PostMapping("/{id}/iniciar")
    public String iniciarLot(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            lotProveidorService.iniciarLot(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("lots.flash.iniciat", null, locale)
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );
        }

        return "redirect:/web/lots";
    }

    @PostMapping("/{id}/finalitzar")
    public String finalitzarLot(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            lotProveidorService.finalitzarLot(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("lots.flash.finalitzat", null, locale)
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );
        }

        return "redirect:/web/lots";
    }
}
