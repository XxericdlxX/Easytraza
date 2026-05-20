package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.config.IdentificadorFiscalValidator;
import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.model.enums.TipusClient;
import cat.copernic.easytraza_backend.service.ClientService;
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
 * Controlador `ClientWebController` del projecte EasyTraza.
 */
@Controller
@RequestMapping("/web/clients")
public class ClientWebController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Executa l'operació `llistar`.
     *
     * @param document paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param tipus paràmetre necessari per a l'operació.
     * @param telefon paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping
    public String llistar(@RequestParam(required = false) String document,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String tipus,
            @RequestParam(required = false) String telefon,
            @RequestParam(required = false) String email,
            Model model) {

        model.addAttribute("clients", clientService.buscar(document, nom, tipus, telefon, email));
        model.addAttribute("tipusClients", clientService.obtenirTipusClients());
        model.addAttribute("clientService", clientService);

        model.addAttribute("document", document);
        model.addAttribute("nom", nom);
        model.addAttribute("tipus", tipus);
        model.addAttribute("telefon", telefon);
        model.addAttribute("email", email);

        model.addAttribute("currentPath", "/web/clients");

        return "clients/llistar-clients";
    }

    /**
     * Executa l'operació `crear`.
     *
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("client", new Client());
        prepararModelFormulari(model);

        return "clients/crear-clients";
    }

    /**
     * Executa l'operació `guardar`.
     *
     * @param client paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/guardar")
    public String guardar(@Valid Client client,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        validarDocumentFiscalClient(client, bindingResult, locale, true, null);
        validarTipusClientAltres(client, bindingResult, locale);

        if (bindingResult.hasErrors()) {
            return tornarCrearAmbErrors(client, model, locale);
        }

        client.setNif(IdentificadorFiscalValidator.normalitzar(client.getNif()));
        clientService.save(client);
        redirectAttributes.addFlashAttribute("missatgeExit", missatge("clients.flash.creat", locale));

        return "redirect:/web/clients";
    }

    /**
     * Executa l'operació `editar`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/editar/{nif}")
    public String editar(@PathVariable String nif,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Client client = clientService.findById(nif).orElse(null);

        if (client == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("clients.flash.no.trobat", locale));
            return "redirect:/web/clients";
        }

        model.addAttribute("client", client);
        prepararModelFormulari(model);

        return "clients/editar-clients";
    }

    /**
     * Executa l'operació `actualitzar`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @param client paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/actualitzar/{nif}")
    public String actualitzar(@PathVariable String nif,
            @Valid Client client,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        client.setNif(nif);
        validarDocumentFiscalClient(client, bindingResult, locale, false, nif);
        validarTipusClientAltres(client, bindingResult, locale);

        if (bindingResult.hasErrors()) {
            return tornarEditarAmbErrors(client, model, locale);
        }

        Client actualitzat = clientService.update(nif, client);

        if (actualitzat == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("clients.flash.no.trobat", locale));
            return "redirect:/web/clients";
        }

        redirectAttributes.addFlashAttribute("missatgeExit", missatge("clients.flash.actualitzat", locale));
        return "redirect:/web/clients";
    }

    /**
     * Executa l'operació `eliminar`.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/eliminar/{nif}")
    public String eliminar(@PathVariable String nif,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            clientService.deleteById(nif);
            redirectAttributes.addFlashAttribute("missatgeExit", missatge("clients.flash.eliminat", locale));
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("clients.error.eliminar.relacions", locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("clients.error.eliminar.generic", locale));
        }

        return "redirect:/web/clients";
    }

    /**
     * Executa l'operació `validarDocumentFiscalClient`.
     *
     * @param client paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @param esCreacio paràmetre necessari per a l'operació.
     * @param nifOriginal paràmetre necessari per a l'operació.
     */
    private void validarDocumentFiscalClient(Client client,
            BindingResult bindingResult,
            Locale locale,
            boolean esCreacio,
            String nifOriginal) {

        String documentNormalitzat = esCreacio
                ? IdentificadorFiscalValidator.normalitzar(client.getNif())
                : IdentificadorFiscalValidator.normalitzar(nifOriginal);

        if (documentNormalitzat == null) {
            afegirErrorSiNoExisteix(bindingResult, "nif", "clients.nif.obligatori", locale);
            return;
        }

        if (!IdentificadorFiscalValidator.esDocumentFiscalValid(documentNormalitzat)) {
            afegirErrorSiNoExisteix(bindingResult, "nif", "clients.nif.invalid", locale);
            return;
        }

        if (esCreacio && clientService.existsById(documentNormalitzat)) {
            afegirErrorSiNoExisteix(bindingResult, "nif", "clients.error.duplicat", locale);
        }
    }

    /**
     * Executa l'operació `validarTipusClientAltres`.
     *
     * @param client paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     */
    private void validarTipusClientAltres(Client client, BindingResult bindingResult, Locale locale) {
        if (client.getTipusClient() == TipusClient.ALTRES
                && (client.getTipusClientAltres() == null || client.getTipusClientAltres().isBlank())) {
            afegirErrorSiNoExisteix(bindingResult, "tipusClientAltres", "clients.tipus.altres.obligatori", locale);
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
     * @param client paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String tornarCrearAmbErrors(Client client, Model model, Locale locale) {
        model.addAttribute("errorNegoci", missatge("error.validacio", locale));
        model.addAttribute("client", client);
        prepararModelFormulari(model);
        return "clients/crear-clients";
    }

    /**
     * Executa l'operació `tornarEditarAmbErrors`.
     *
     * @param client paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String tornarEditarAmbErrors(Client client, Model model, Locale locale) {
        model.addAttribute("errorNegoci", missatge("error.validacio", locale));
        model.addAttribute("client", client);
        prepararModelFormulari(model);
        return "clients/editar-clients";
    }

    /**
     * Executa l'operació `prepararModelFormulari`.
     *
     * @param model paràmetre necessari per a l'operació.
     */
    private void prepararModelFormulari(Model model) {
        model.addAttribute("tipusClients", clientService.obtenirTipusClients());
        model.addAttribute("currentPath", "/web/clients");
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
