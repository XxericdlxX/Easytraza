package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.model.enums.TipusClient;
import cat.copernic.easytraza_backend.service.ClientService;
import cat.copernic.easytraza_backend.config.IdentificadorFiscalValidator;
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
@RequestMapping("/web/clients")
public class ClientWebController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private MessageSource messageSource;

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

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("client", new Client());
        prepararModelFormulari(model);

        return "clients/crear-clients";
    }

    @PostMapping("/guardar")
    public String guardar(Client client,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        String error = validarClient(client, locale, true, null);

        if (error != null) {
            return tornarCrearAmbError(client, model, error);
        }

        client.setNif(IdentificadorFiscalValidator.normalitzar(client.getNif()));
        clientService.save(client);
        redirectAttributes.addFlashAttribute("missatgeExit", missatge("clients.flash.creat", locale));

        return "redirect:/web/clients";
    }

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

    @PostMapping("/actualitzar/{nif}")
    public String actualitzar(@PathVariable String nif,
            Client client,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        String error = validarClient(client, locale, false, nif);

        if (error != null) {
            client.setNif(nif);
            return tornarEditarAmbError(client, model, error);
        }

        Client actualitzat = clientService.update(nif, client);

        if (actualitzat == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("clients.flash.no.trobat", locale));
            return "redirect:/web/clients";
        }

        redirectAttributes.addFlashAttribute("missatgeExit", missatge("clients.flash.actualitzat", locale));
        return "redirect:/web/clients";
    }

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

    private String validarClient(Client client, Locale locale, boolean esCreacio, String nifOriginal) {
        String documentNormalitzat = esCreacio
                ? IdentificadorFiscalValidator.normalitzar(client.getNif())
                : IdentificadorFiscalValidator.normalitzar(nifOriginal);

        if (documentNormalitzat == null) {
            return missatge("clients.nif.obligatori", locale);
        }

        if (!IdentificadorFiscalValidator.esDocumentFiscalValid(documentNormalitzat)) {
            return missatge("clients.nif.invalid", locale);
        }

        if (esCreacio && clientService.existsById(documentNormalitzat)) {
            return missatge("clients.error.duplicat", locale);
        }

        if (client.getTipusClient() == null) {
            return missatge("clients.tipus.obligatori", locale);
        }

        if (client.getTipusClient() == TipusClient.ALTRES
                && (client.getTipusClientAltres() == null || client.getTipusClientAltres().isBlank())) {
            return missatge("clients.tipus.altres.obligatori", locale);
        }

        return null;
    }

    private String tornarCrearAmbError(Client client, Model model, String error) {
        model.addAttribute("errorNegoci", error);
        model.addAttribute("client", client);
        prepararModelFormulari(model);
        return "clients/crear-clients";
    }

    private String tornarEditarAmbError(Client client, Model model, String error) {
        model.addAttribute("errorNegoci", error);
        model.addAttribute("client", client);
        prepararModelFormulari(model);
        return "clients/editar-clients";
    }

    private void prepararModelFormulari(Model model) {
        model.addAttribute("tipusClients", clientService.obtenirTipusClients());
        model.addAttribute("currentPath", "/web/clients");
    }

    private String missatge(String codi, Locale locale) {
        return messageSource.getMessage(codi, null, codi, locale);
    }
}
