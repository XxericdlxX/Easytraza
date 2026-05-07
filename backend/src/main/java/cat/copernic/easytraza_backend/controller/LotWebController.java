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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @RequestParam(required = false, defaultValue = "dataRecepcio") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Model model) {

        model.addAttribute(
                "lots",
                lotProveidorService.cercar(codiLot, estat, materiaPrimaId, dataRecepcio, sortField, sortDir)
        );

        model.addAttribute("materiesPrimeres", materiaPrimaRepository.findAll());
        model.addAttribute("estatsLot", EstatLot.values());

        model.addAttribute("codiLot", codiLot);
        model.addAttribute("estat", estat);
        model.addAttribute("materiaPrimaId", materiaPrimaId);
        model.addAttribute("dataRecepcio", dataRecepcio);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");

        model.addAttribute("currentPath", "/web/lots");

        return "lots/llistar-lots";
    }

    @GetMapping({"/{id}/iniciar", "/iniciar/{id}"})
    public String iniciarLotGet(
            @PathVariable Long id,
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) Long albaraId,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        return executarIniciLot(id, origen, albaraId, redirectAttributes, locale);
    }

    @PostMapping({"/{id}/iniciar", "/iniciar/{id}"})
    public String iniciarLotPost(
            @PathVariable Long id,
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) Long albaraId,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        return executarIniciLot(id, origen, albaraId, redirectAttributes, locale);
    }

    @GetMapping({"/{id}/finalitzar", "/finalitzar/{id}"})
    public String finalitzarLotGet(
            @PathVariable Long id,
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) Long albaraId,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        return executarFinalitzacioLot(id, origen, albaraId, redirectAttributes, locale);
    }

    @PostMapping({"/{id}/finalitzar", "/finalitzar/{id}"})
    public String finalitzarLotPost(
            @PathVariable Long id,
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) Long albaraId,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        return executarFinalitzacioLot(id, origen, albaraId, redirectAttributes, locale);
    }

    private String executarIniciLot(
            Long id,
            String origen,
            Long albaraId,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            lotProveidorService.iniciarLot(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    missatge("lots.flash.iniciat", locale)
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    missatge(ex.getMessage(), locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    missatge("lots.error.no.trobat", locale)
            );
        }

        return obtenirRedireccio(origen, albaraId);
    }

    private String executarFinalitzacioLot(
            Long id,
            String origen,
            Long albaraId,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            lotProveidorService.finalitzarLot(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    missatge("lots.flash.finalitzat", locale)
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    missatge(ex.getMessage(), locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    missatge("lots.error.no.trobat", locale)
            );
        }

        return obtenirRedireccio(origen, albaraId);
    }

    private String obtenirRedireccio(String origen, Long albaraId) {
        if ("albara".equalsIgnoreCase(origen) && albaraId != null) {
            return "redirect:/web/albarans-proveidor/veure/" + albaraId;
        }

        return "redirect:/web/lots";
    }

    private String missatge(String codi, Locale locale) {
        return messageSource.getMessage(codi, null, codi, locale);
    }
}
