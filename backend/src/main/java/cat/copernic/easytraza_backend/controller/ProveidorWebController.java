package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.config.IdentificadorFiscalValidator;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.service.ProveidorService;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/proveidors")
public class ProveidorWebController {

    @Autowired
    private ProveidorService proveidorService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistar(@RequestParam(required = false) String document,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String telefon,
            @RequestParam(required = false) String email,
            Model model) {

        model.addAttribute("proveidors", proveidorService.buscar(document, nom, telefon, email));

        model.addAttribute("document", document);
        model.addAttribute("nom", nom);
        model.addAttribute("telefon", telefon);
        model.addAttribute("email", email);

        model.addAttribute("currentPath", "/web/proveidors");

        return "proveidors/llistar-proveidors";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("proveidor", new Proveidor());
        model.addAttribute("currentPath", "/web/proveidors");

        return "proveidors/crear-proveidors";
    }

    @PostMapping("/guardar")
    public String guardar(Proveidor proveidor,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        String documentNormalitzat = IdentificadorFiscalValidator.normalitzar(proveidor.getCif());

        if (documentNormalitzat == null) {
            return tornarCrearAmbError(proveidor, model, locale, "proveidors.cif.obligatori");
        }

        if (!IdentificadorFiscalValidator.esDocumentFiscalValid(documentNormalitzat)) {
            return tornarCrearAmbError(proveidor, model, locale, "proveidors.cif.invalid");
        }

        proveidor.setCif(documentNormalitzat);

        if (proveidorService.existsById(documentNormalitzat)) {
            return tornarCrearAmbError(proveidor, model, locale, "proveidors.error.duplicat");
        }

        proveidorService.save(proveidor);
        redirectAttributes.addFlashAttribute("missatgeExit", missatge("proveidors.flash.creat", locale));

        return "redirect:/web/proveidors";
    }

    @GetMapping("/editar/{cif}")
    public String editar(@PathVariable String cif,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Proveidor proveidor = proveidorService.findById(cif).orElse(null);

        if (proveidor == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("proveidors.flash.no.trobat", locale));
            return "redirect:/web/proveidors";
        }

        model.addAttribute("proveidor", proveidor);
        model.addAttribute("currentPath", "/web/proveidors");

        return "proveidors/editar-proveidors";
    }

    @PostMapping("/actualitzar/{cif}")
    public String actualitzar(@PathVariable String cif,
            Proveidor proveidor,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String documentNormalitzat = IdentificadorFiscalValidator.normalitzar(cif);

        if (documentNormalitzat == null || !IdentificadorFiscalValidator.esDocumentFiscalValid(documentNormalitzat)) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("proveidors.cif.invalid", locale));
            return "redirect:/web/proveidors";
        }

        proveidor.setCif(documentNormalitzat);

        Proveidor actualitzat = proveidorService.update(documentNormalitzat, proveidor);

        if (actualitzat == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("proveidors.flash.no.trobat", locale));
            return "redirect:/web/proveidors";
        }

        redirectAttributes.addFlashAttribute("missatgeExit", missatge("proveidors.flash.actualitzat", locale));
        return "redirect:/web/proveidors";
    }

    @GetMapping("/eliminar/{cif}")
    public String eliminar(@PathVariable String cif,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            proveidorService.deleteById(cif);
            redirectAttributes.addFlashAttribute("missatgeExit", missatge("proveidors.flash.eliminat", locale));
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("proveidors.error.eliminar.relacions", locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("proveidors.error.eliminar.generic", locale));
        }

        return "redirect:/web/proveidors";
    }

    private String tornarCrearAmbError(Proveidor proveidor, Model model, Locale locale, String codiMissatge) {
        model.addAttribute("errorNegoci", missatge(codiMissatge, locale));
        model.addAttribute("proveidor", proveidor);
        model.addAttribute("currentPath", "/web/proveidors");
        return "proveidors/crear-proveidors";
    }

    private String missatge(String codi, Locale locale) {
        return messageSource.getMessage(codi, null, codi, locale);
    }
}
