package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.ComandaDto;
import cat.copernic.easytraza_backend.dto.LiniaComandaDto;
import cat.copernic.easytraza_backend.model.Comanda;
import cat.copernic.easytraza_backend.model.enums.EstatComanda;
import cat.copernic.easytraza_backend.repository.ClientRepository;
import cat.copernic.easytraza_backend.repository.ProducteRepository;
import cat.copernic.easytraza_backend.service.ComandaService;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web del CRUD de comandes.
 */
@Controller
@RequestMapping("/web/comandes")
public class ComandaWebController {

    @Autowired
    private ComandaService comandaService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProducteRepository producteRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistar(@RequestParam(required = false) String clientNif,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataComanda,
            @RequestParam(required = false) EstatComanda estat,
            @RequestParam(required = false) Long producteId,
            Model model) {

        model.addAttribute("comandes", comandaService.buscar(clientNif, dataComanda, estat, producteId));
        model.addAttribute("clientNif", clientNif);
        model.addAttribute("dataComanda", dataComanda);
        model.addAttribute("estat", estat);
        model.addAttribute("producteId", producteId);
        model.addAttribute("estatsComanda", EstatComanda.values());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("productes", producteRepository.findAll());
        model.addAttribute("currentPath", "/web/comandes");

        return "comandes/llistar-comandes";
    }

    /**
     * Mostra el detall de l'element indicat.
     *
     * @param id paràmetre necessari per executar l'operació.
     * @param model paràmetre necessari per executar l'operació.
     * @param redirectAttributes paràmetre necessari per executar l'operació.
     * @param locale paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/veure/{id}")
    public String veure(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<Comanda> comanda = comandaService.findById(id);

        if (comanda.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("comandes.flash.no.trobada", null, locale)
            );
            return "redirect:/web/comandes";
        }

        model.addAttribute("comandaDetall", comanda.get());
        model.addAttribute("currentPath", "/web/comandes");

        return "comandes/veure-comanda";
    }

    /**
     * Mostra o prepara la pantalla de creació corresponent.
     *
     * @param model paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/crear")
    public String crear(Model model) {
        ComandaDto dto = new ComandaDto();
        dto.setDataComanda(LocalDate.now());
        dto.setEstat(EstatComanda.PENDENT);
        dto.getLinies().add(new LiniaComandaDto());

        carregarDadesFormulari(model);
        model.addAttribute("comanda", dto);
        model.addAttribute("currentPath", "/web/comandes");

        return "comandes/crear-comanda";
    }

    /**
     * Guarda la informació indicada al sistema.
     *
     * @param dto paràmetre necessari per executar l'operació.
     * @param model paràmetre necessari per executar l'operació.
     * @param redirectAttributes paràmetre necessari per executar l'operació.
     * @param locale paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("comanda") ComandaDto dto,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String errorNegoci = comandaService.validarComanda(dto);

        if (errorNegoci != null) {
            carregarDadesFormulari(model);
            comandaService.assegurarMinimUnaLinia(dto);
            model.addAttribute("comanda", dto);
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            model.addAttribute("currentPath", "/web/comandes");
            return "comandes/crear-comanda";
        }

        Comanda comanda = comandaService.convertirDtoAEntity(dto);
        comandaService.save(comanda);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("comandes.flash.creada", null, locale)
        );

        return "redirect:/web/comandes";
    }

    /**
     * Mostra o processa la pantalla d'edició corresponent.
     *
     * @param id paràmetre necessari per executar l'operació.
     * @param model paràmetre necessari per executar l'operació.
     * @param redirectAttributes paràmetre necessari per executar l'operació.
     * @param locale paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<Comanda> comanda = comandaService.findById(id);

        if (comanda.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("comandes.flash.no.trobada", null, locale)
            );
            return "redirect:/web/comandes";
        }

        if (comanda.get().getAlbaraClient() != null) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("comandes.error.editar.amb.albara", null, locale)
            );
            return "redirect:/web/comandes/veure/" + id;
        }

        carregarDadesFormulari(model);
        model.addAttribute("comanda", comandaService.convertirEntityADto(comanda.get()));
        model.addAttribute("currentPath", "/web/comandes");

        return "comandes/editar-comanda";
    }

    /**
     * Actualitza la informació indicada al sistema.
     *
     * @param id paràmetre necessari per executar l'operació.
     * @param dto paràmetre necessari per executar l'operació.
     * @param model paràmetre necessari per executar l'operació.
     * @param redirectAttributes paràmetre necessari per executar l'operació.
     * @param locale paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/actualitzar/{id}")
    public String actualitzar(@PathVariable Long id,
            @ModelAttribute("comanda") ComandaDto dto,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String errorNegoci = comandaService.validarComanda(dto);

        if (errorNegoci != null) {
            carregarDadesFormulari(model);
            comandaService.assegurarMinimUnaLinia(dto);
            model.addAttribute("comanda", dto);
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            model.addAttribute("currentPath", "/web/comandes");
            return "comandes/editar-comanda";
        }

        try {
            Comanda comanda = comandaService.convertirDtoAEntity(dto);
            comandaService.update(id, comanda);

            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("comandes.flash.actualitzada", null, locale)
            );
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );
            return "redirect:/web/comandes/veure/" + id;
        }

        return "redirect:/web/comandes";
    }

    /**
     * Executa l'operació `generarAlbara`.
     *
     * @param id paràmetre necessari per executar l'operació.
     * @param redirectAttributes paràmetre necessari per executar l'operació.
     * @param locale paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/generar-albara/{id}")
    public String generarAlbara(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            Comanda comanda = comandaService.generarAlbaraClient(id);

            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("comandes.flash.albara.generat", null, locale)
            );

            if (comanda.getAlbaraClient() != null) {
                return "redirect:/web/albarans-client/veure/" + comanda.getAlbaraClient().getId();
            }

            return "redirect:/web/comandes/veure/" + id;

        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );
            return "redirect:/web/comandes/veure/" + id;
        }
    }

    /**
     * Elimina o dona de baixa la informació indicada.
     *
     * @param id paràmetre necessari per executar l'operació.
     * @param redirectAttributes paràmetre necessari per executar l'operació.
     * @param locale paràmetre necessari per executar l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            comandaService.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("comandes.flash.eliminada", null, locale)
            );
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("comandes.error.eliminar.relacions", null, locale)
            );
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("comandes.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/comandes";
    }

    private void carregarDadesFormulari(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("productes", producteRepository.findAll());
        model.addAttribute("estatsComanda", EstatComanda.values());
    }
}
