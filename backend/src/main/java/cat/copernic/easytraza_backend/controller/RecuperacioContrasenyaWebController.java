package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.RecuperacioContrasenyaDto;
import cat.copernic.easytraza_backend.dto.RestablirContrasenyaDto;
import cat.copernic.easytraza_backend.service.RecuperacioContrasenyaService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Controlador `RecuperacioContrasenyaWebController` del projecte EasyTraza.
 */
@Controller
public class RecuperacioContrasenyaWebController {

    @Autowired
    private RecuperacioContrasenyaService recuperacioContrasenyaService;

    /**
     * Executa l'operació `mostrarSollicitudRecuperacio`.
     *
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/recuperar-contrasenya")
    public String mostrarSollicitudRecuperacio(Model model) {
        if (!model.containsAttribute("recuperacioContrasenyaDto")) {
            model.addAttribute("recuperacioContrasenyaDto", new RecuperacioContrasenyaDto());
        }
        return "auth/recuperar-contrasenya";
    }

    /**
     * Executa l'operació `generarSollicitudRecuperacio`.
     *
     * @param recuperacioContrasenyaDto paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/recuperar-contrasenya")
    public String generarSollicitudRecuperacio(
            @Valid @ModelAttribute("recuperacioContrasenyaDto") RecuperacioContrasenyaDto recuperacioContrasenyaDto,
            BindingResult bindingResult,
            Model model,
            Locale locale
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/recuperar-contrasenya";
        }

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        recuperacioContrasenyaService.sollicitarRecuperacio(recuperacioContrasenyaDto.getEmail(), baseUrl, locale);
        model.addAttribute("recuperacioSollicitada", true);
        model.addAttribute("recuperacioContrasenyaDto", new RecuperacioContrasenyaDto());
        return "auth/recuperar-contrasenya";
    }

    /**
     * Executa l'operació `mostrarRestablimentContrasenya`.
     *
     * @param token paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/restablir-contrasenya")
    public String mostrarRestablimentContrasenya(@RequestParam(required = false) String token, Model model) {
        RestablirContrasenyaDto restablirContrasenyaDto = new RestablirContrasenyaDto(token);
        model.addAttribute("restablirContrasenyaDto", restablirContrasenyaDto);
        model.addAttribute("tokenValid", recuperacioContrasenyaService.tokenValid(token));
        return "auth/restablir-contrasenya";
    }

    /**
     * Executa l'operació `restablirContrasenya`.
     *
     * @param restablirContrasenyaDto paràmetre necessari per a l'operació.
     * @param bindingResult paràmetre necessari per a l'operació.
     * @param model paràmetre necessari per a l'operació.
     * @param redirectAttributes paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @PostMapping("/restablir-contrasenya")
    public String restablirContrasenya(
            @Valid @ModelAttribute("restablirContrasenyaDto") RestablirContrasenyaDto restablirContrasenyaDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        boolean tokenValid = recuperacioContrasenyaService.tokenValid(restablirContrasenyaDto.getToken());
        model.addAttribute("tokenValid", tokenValid);

        if (bindingResult.hasErrors()) {
            return "auth/restablir-contrasenya";
        }

        String errorKey = recuperacioContrasenyaService.validarRestabliment(restablirContrasenyaDto);
        if (errorKey != null) {
            model.addAttribute("recuperacioError", errorKey);
            model.addAttribute("tokenValid", recuperacioContrasenyaService.tokenValid(restablirContrasenyaDto.getToken()));
            return "auth/restablir-contrasenya";
        }

        boolean actualitzat = recuperacioContrasenyaService.restablirContrasenya(restablirContrasenyaDto);
        if (!actualitzat) {
            model.addAttribute("recuperacioError", "recuperacio.error.token.invalid");
            model.addAttribute("tokenValid", false);
            return "auth/restablir-contrasenya";
        }

        redirectAttributes.addAttribute("resetOk", "true");
        return "redirect:/login";
    }
}
