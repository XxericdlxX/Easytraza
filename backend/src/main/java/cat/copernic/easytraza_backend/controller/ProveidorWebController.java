package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.config.IdentificadorFiscalValidator;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.service.ProveidorService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador `ProveidorWebController` del projecte EasyTraza.
 */
@Controller
@RequestMapping("/web/proveidors")
public class ProveidorWebController {

    @Autowired
    private ProveidorService proveidorService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Executa l'operació `llistar`.
     *
     * @param document paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param telefon paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `crear`.
     *
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("proveidor", new Proveidor());
        model.addAttribute("currentPath", "/web/proveidors");

        return "proveidors/crear-proveidors";
    }

    /**
     * Executa l'operació `guardar`.
     *
     * @param proveidor paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/guardar")
    public String guardar(@Valid Proveidor proveidor,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        validarDocumentFiscalProveidor(proveidor, bindingResult, locale, true, null);

        if (bindingResult.hasErrors()) {
            return tornarCrearAmbErrors(proveidor, model, locale);
        }

        proveidor.setCif(IdentificadorFiscalValidator.normalitzar(proveidor.getCif()));
        proveidorService.save(proveidor);
        redirectAttributes.addFlashAttribute("missatgeExit", missatge("proveidors.flash.creat", locale));

        return "redirect:/web/proveidors";
    }

    /**
     * Executa l'operació `editar`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `actualitzar`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @param proveidor paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/actualitzar/{cif}")
    public String actualitzar(@PathVariable String cif,
            @Valid Proveidor proveidor,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        proveidor.setCif(cif);
        validarDocumentFiscalProveidor(proveidor, bindingResult, locale, false, cif);

        if (bindingResult.hasErrors()) {
            return tornarEditarAmbErrors(proveidor, model, locale);
        }

        Proveidor actualitzat = proveidorService.update(cif, proveidor);

        if (actualitzat == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("proveidors.flash.no.trobat", locale));
            return "redirect:/web/proveidors";
        }

        redirectAttributes.addFlashAttribute("missatgeExit", missatge("proveidors.flash.actualitzat", locale));
        return "redirect:/web/proveidors";
    }

    /**
     * Executa l'operació `eliminar`.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `validarDocumentFiscalProveidor`.
     *
     * @param proveidor paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @param esCreacio paràmetre necessari per a l'operació.
     * @param cifOriginal paràmetre necessari per a l'operació.
     */
    private void validarDocumentFiscalProveidor(Proveidor proveidor,
            BindingResult bindingResult,
            Locale locale,
            boolean esCreacio,
            String cifOriginal) {

        String documentNormalitzat = esCreacio
                ? IdentificadorFiscalValidator.normalitzar(proveidor.getCif())
                : IdentificadorFiscalValidator.normalitzar(cifOriginal);

        if (documentNormalitzat == null) {
            afegirErrorSiNoExisteix(bindingResult, "cif", "proveidors.cif.obligatori", locale);
            return;
        }

        if (!IdentificadorFiscalValidator.esDocumentFiscalValid(documentNormalitzat)) {
            afegirErrorSiNoExisteix(bindingResult, "cif", "proveidors.cif.invalid", locale);
            return;
        }

        if (esCreacio && proveidorService.existsById(documentNormalitzat)) {
            afegirErrorSiNoExisteix(bindingResult, "cif", "proveidors.error.duplicat", locale);
        }
    }

    /**
     * Executa l'operació `afegirErrorSiNoExisteix`.
     *
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param camp paràmetre necessari per a l'operació.
     * @param codiMissatge paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     */
    private void afegirErrorSiNoExisteix(BindingResult bindingResult, String camp, String codiMissatge, Locale locale) {
        if (!bindingResult.hasFieldErrors(camp)) {
            bindingResult.rejectValue(camp, codiMissatge, missatge(codiMissatge, locale));
        }
    }

    /**
     * Executa l'operació `tornarCrearAmbErrors`.
     *
     * @param proveidor paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String tornarCrearAmbErrors(Proveidor proveidor, Model model, Locale locale) {
        model.addAttribute("errorNegoci", missatge("error.validacio", locale));
        model.addAttribute("proveidor", proveidor);
        model.addAttribute("currentPath", "/web/proveidors");
        return "proveidors/crear-proveidors";
    }

    /**
     * Executa l'operació `tornarEditarAmbErrors`.
     *
     * @param proveidor paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String tornarEditarAmbErrors(Proveidor proveidor, Model model, Locale locale) {
        model.addAttribute("errorNegoci", missatge("error.validacio", locale));
        model.addAttribute("proveidor", proveidor);
        model.addAttribute("currentPath", "/web/proveidors");
        return "proveidors/editar-proveidors";
    }

    /**
     * Executa l'operació `missatge`.
     *
     * @param codi paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String missatge(String codi, Locale locale) {
        return messageSource.getMessage(codi, null, codi, locale);
    }
}
