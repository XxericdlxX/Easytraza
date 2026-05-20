package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.AlbaraClientDto;
import cat.copernic.easytraza_backend.dto.LiniaClientDto;
import cat.copernic.easytraza_backend.model.AlbaraClient;
import cat.copernic.easytraza_backend.model.enums.EstatAlbaraClient;
import cat.copernic.easytraza_backend.repository.ClientRepository;
import cat.copernic.easytraza_backend.repository.ProducteRepository;
import cat.copernic.easytraza_backend.service.AlbaraClientService;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador `AlbaraClientWebController` del projecte EasyTraza.
 */
@Controller
@RequestMapping("/web/albarans-client")
public class AlbaraClientWebController {

    @Autowired
    private AlbaraClientService albaraClientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProducteRepository producteRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Executa l'operació `llistar`.
     *
     * @param clientNif paràmetre necessari per a l'operació.
     * @param dataProduccio paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping
    public String llistar(
            @RequestParam(required = false) String clientNif,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataProduccio,
            @RequestParam(required = false) EstatAlbaraClient estat,
            Model model) {

        model.addAttribute("albarans", albaraClientService.buscar(clientNif, dataProduccio, estat));
        model.addAttribute("clientNif", clientNif);
        model.addAttribute("dataProduccio", dataProduccio);
        model.addAttribute("estat", estat);
        model.addAttribute("estatsAlbaraClient", EstatAlbaraClient.values());
        model.addAttribute("currentPath", "/web/albarans-client");

        return "albarans_client/llistar-albarans-client";
    }

    /**
     * Executa l'operació `veureDetall`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/veure/{id}")
    public String veureDetall(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<AlbaraClient> albara = albaraClientService.findById(id);

        if (albara.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.client.flash.no.trobat", null, locale)
            );
            return "redirect:/web/albarans-client";
        }

        model.addAttribute("albaraDetall", albara.get());
        model.addAttribute("currentPath", "/web/albarans-client");

        return "albarans_client/veure-albara-client";
    }

    /**
     * Executa l'operació `mostrarFormulariCrear`.
     *
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/crear")
    public String mostrarFormulariCrear(Model model) {
        AlbaraClientDto dto = new AlbaraClientDto();
        dto.setDataProduccio(LocalDate.now());
        dto.getLinies().add(new LiniaClientDto());

        carregarDadesFormulari(model);
        model.addAttribute("albara", dto);
        model.addAttribute("currentPath", "/web/albarans-client");

        return "albarans_client/crear-albara-client";
    }

    /**
     * Executa l'operació `guardar`.
     *
     * @param dto paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("albara") AlbaraClientDto dto,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String errorNegoci = albaraClientService.validarAlbara(dto, null);

        if (errorNegoci != null) {
            carregarDadesFormulari(model);
            albaraClientService.assegurarMinimUnaLinia(dto);
            model.addAttribute("albara", dto);
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            model.addAttribute("currentPath", "/web/albarans-client");
            return "albarans_client/crear-albara-client";
        }

        AlbaraClient entity = albaraClientService.convertirDtoAEntity(dto);
        albaraClientService.save(entity);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("albara.client.flash.creat", null, locale)
        );

        return "redirect:/web/albarans-client";
    }

    /**
     * Executa l'operació `mostrarFormulariEditar`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditar(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<AlbaraClient> albara = albaraClientService.findById(id);

        if (albara.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.client.flash.no.trobat", null, locale)
            );
            return "redirect:/web/albarans-client";
        }

        if (albara.get().getEstat() == EstatAlbaraClient.LLIURAT) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.client.error.modificar.lliurat", null, locale)
            );
            return "redirect:/web/albarans-client/veure/" + id;
        }

        carregarDadesFormulari(model);
        model.addAttribute("albara", albaraClientService.convertirEntityADto(albara.get()));
        model.addAttribute("currentPath", "/web/albarans-client");

        return "albarans_client/editar-albara-client";
    }

    /**
     * Executa l'operació `actualitzar`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param dto paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/actualitzar/{id}")
    public String actualitzar(@PathVariable Long id,
            @ModelAttribute("albara") AlbaraClientDto dto,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String errorNegoci = albaraClientService.validarAlbara(dto, id);

        if (errorNegoci != null) {
            carregarDadesFormulari(model);
            albaraClientService.assegurarMinimUnaLinia(dto);
            model.addAttribute("albara", dto);
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            model.addAttribute("currentPath", "/web/albarans-client");
            return "albarans_client/editar-albara-client";
        }

        AlbaraClient entity = albaraClientService.convertirDtoAEntity(dto);
        albaraClientService.update(id, entity);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("albara.client.flash.actualitzat", null, locale)
        );

        return "redirect:/web/albarans-client";
    }

    /**
     * Executa l'operació `marcarComLliurat`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/lliurar/{id}")
    public String marcarComLliurat(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            albaraClientService.marcarComLliurat(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("albara.client.flash.lliurat", null, locale)
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );
        }

        return "redirect:/web/albarans-client/veure/" + id;
    }

    /**
     * Executa l'operació `eliminar`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            albaraClientService.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("albara.client.flash.eliminat", null, locale)
            );
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.client.error.eliminar.relacions", null, locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.client.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/albarans-client";
    }

    /**
     * Executa l'operació `carregarDadesFormulari`.
     *
     * @param model paràmetre necessari per a l'operació.
     */
    private void carregarDadesFormulari(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("productes", producteRepository.findAll());
    }
}
