package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.GraficProductesVenutsDto;
import cat.copernic.easytraza_backend.service.GraficProductesVenutsService;
import cat.copernic.easytraza_backend.service.ProducteService;
import java.time.YearMonth;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/web/grafic-productes-venuts")
public class GraficProductesVenutsWebController {

    @Autowired
    private GraficProductesVenutsService graficProductesVenutsService;

    @Autowired
    private ProducteService producteService;

    @GetMapping
    public String veureGraficProductesVenuts(
            @RequestParam(required = false) String mes,
            @RequestParam(required = false) Long producteId,
            Model model,
            Locale locale) {

        YearMonth mesSeleccionat = graficProductesVenutsService.resoldreMesSeleccionat(mes);
        GraficProductesVenutsDto grafic = graficProductesVenutsService.obtenirGraficMensual(
                mesSeleccionat,
                producteId
        );

        model.addAttribute("mesos", graficProductesVenutsService.obtenirUltimsDotzeMesos(locale));
        model.addAttribute("mesSeleccionat", mesSeleccionat.toString());
        model.addAttribute("productes", producteService.findAll());
        model.addAttribute("producteId", producteId);
        model.addAttribute("dies", grafic.getDies());
        model.addAttribute("quantitats", grafic.getQuantitats());
        model.addAttribute("totalUnitats", grafic.getTotalUnitats());
        model.addAttribute("currentPath", "/web/grafic-productes-venuts");

        return "analitica/grafic-productes-venuts";
    }
}
