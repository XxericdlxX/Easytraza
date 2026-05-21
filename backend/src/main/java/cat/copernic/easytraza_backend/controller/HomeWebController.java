package cat.copernic.easytraza_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador `HomeWebController` del projecte EasyTraza.
 */
@Controller
public class HomeWebController {

    /**
     * Executa l'operació `home`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
