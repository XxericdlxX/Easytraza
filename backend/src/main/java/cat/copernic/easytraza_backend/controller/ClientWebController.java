package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.ClientDto;
import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.model.enums.TipusClient;
import cat.copernic.easytraza_backend.service.ClientService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/clients")
public class ClientWebController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistarClients(Model model) {
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("currentPath", "/web/clients");
        return "clients/llistar-clients";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrearClient(Model model) {
        model.addAttribute("client", new ClientDto());
        model.addAttribute("tipusClients", TipusClient.values());
        return "clients/crear-clients";
    }

    @PostMapping("/guardar")
    public String guardarClient(@Valid @ModelAttribute("client") ClientDto clientDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            model.addAttribute("tipusClients", TipusClient.values());
            return "clients/crear-clients";
        }

        String errorNegoci = clientService.validarClient(clientDto, null);
        if (errorNegoci != null) {
            model.addAttribute("tipusClients", TipusClient.values());
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "clients/crear-clients";
        }

        Client client = clientService.convertirDtoAEntity(clientDto);
        clientService.save(client);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("clients.flash.creat", null, locale)
        );

        return "redirect:/web/clients";
    }

    @GetMapping("/editar/{nif}")
    public String mostrarFormulariEditarClient(@PathVariable String nif,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<Client> client = clientService.findById(nif);

        if (client.isPresent()) {
            model.addAttribute("client", clientService.convertirEntityADto(client.get()));
            model.addAttribute("tipusClients", TipusClient.values());
            return "clients/editar-clients";
        } else {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("clients.flash.no.trobat", null, locale)
            );
            return "redirect:/web/clients";
        }
    }

    @PostMapping("/actualitzar/{nif}")
    public String actualitzarClient(@PathVariable String nif,
            @Valid @ModelAttribute("client") ClientDto clientDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            model.addAttribute("tipusClients", TipusClient.values());
            return "clients/editar-clients";
        }

        String errorNegoci = clientService.validarClient(clientDto, nif);
        if (errorNegoci != null) {
            model.addAttribute("tipusClients", TipusClient.values());
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            return "clients/editar-clients";
        }

        Client client = clientService.convertirDtoAEntity(clientDto);
        clientService.update(nif, client);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("clients.flash.actualitzat", null, locale)
        );

        return "redirect:/web/clients";
    }

    @GetMapping("/eliminar/{nif}")
    public String eliminarClient(@PathVariable String nif,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            clientService.deleteById(nif);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("clients.flash.eliminat", null, locale)
            );
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("clients.error.eliminar.relacions", null, locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("clients.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/clients";
    }
}
