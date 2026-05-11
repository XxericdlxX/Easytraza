package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.config.IdentificadorFiscalValidator;
import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.service.ClientService;
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
        model.addAttribute("tipusClients", clientService.obtenirTipusClients());
        model.addAttribute("currentPath", "/web/clients");

        return "clients/crear-clients";
    }

    @PostMapping("/guardar")
    public String guardar(Client client,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        String documentNormalitzat = IdentificadorFiscalValidator.normalitzar(client.getNif());

        if (documentNormalitzat == null) {
            return tornarCrearAmbError(client, model, locale, "clients.nif.obligatori");
        }

        if (!IdentificadorFiscalValidator.esDocumentFiscalValid(documentNormalitzat)) {
            return tornarCrearAmbError(client, model, locale, "clients.nif.invalid");
        }

        client.setNif(documentNormalitzat);

        if (clientService.existsById(documentNormalitzat)) {
            return tornarCrearAmbError(client, model, locale, "clients.error.duplicat");
        }

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
        model.addAttribute("tipusClients", clientService.obtenirTipusClients());
        model.addAttribute("currentPath", "/web/clients");

        return "clients/editar-clients";
    }

    @PostMapping("/actualitzar/{nif}")
    public String actualitzar(@PathVariable String nif,
            Client client,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String documentNormalitzat = IdentificadorFiscalValidator.normalitzar(nif);

        if (documentNormalitzat == null || !IdentificadorFiscalValidator.esDocumentFiscalValid(documentNormalitzat)) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("clients.nif.invalid", locale));
            return "redirect:/web/clients";
        }

        client.setNif(documentNormalitzat);

        Client actualitzat = clientService.update(documentNormalitzat, client);

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

    private String tornarCrearAmbError(Client client, Model model, Locale locale, String codiMissatge) {
        model.addAttribute("errorNegoci", missatge(codiMissatge, locale));
        model.addAttribute("client", client);
        model.addAttribute("tipusClients", clientService.obtenirTipusClients());
        model.addAttribute("currentPath", "/web/clients");
        return "clients/crear-clients";
    }

    private String missatge(String codi, Locale locale) {
        return messageSource.getMessage(codi, null, codi, locale);
    }
}
