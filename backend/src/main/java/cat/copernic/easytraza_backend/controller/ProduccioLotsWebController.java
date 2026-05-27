package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.model.Producte;
import cat.copernic.easytraza_backend.service.ProducteService;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador `ProduccioLotsWebController` del projecte EasyTraza.
 */
@Controller
@RequestMapping("/web/produccio-lots")
public class ProduccioLotsWebController {

    @Autowired
    private ProducteService producteService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Executa l'operació `veureProduccioLots`.
     *
     * @param producteId paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping
    public String veureProduccioLots(@RequestParam(required = false) Long producteId,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Producte producteSeleccionat = null;

        if (producteId != null) {
            Optional<Producte> producte = producteService.findById(producteId);

            if (producte.isEmpty()) {
                redirectAttributes.addFlashAttribute(
                        "missatgeError",
                        messageSource.getMessage("productes.flash.no.trobat", null, locale)
                );
                return "redirect:/web/produccio-lots";
            }

            producteSeleccionat = producte.get();
        }

        model.addAttribute("productes", producteService.findAll());
        model.addAttribute("producte", producteSeleccionat);
        model.addAttribute("producteId", producteId);
        model.addAttribute("liniesProduccio", producteId == null
                ? producteService.cercarTotaProduccioLots()
                : producteService.cercarProduccioLotsPerProducte(producteId));
        model.addAttribute("currentPath", "/web/produccio-lots");

        return "productes/produccio-lots-producte";
    }
}
