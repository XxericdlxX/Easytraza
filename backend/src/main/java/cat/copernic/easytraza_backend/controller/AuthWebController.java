package cat.copernic.easytraza_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador `AuthWebController` del projecte EasyTraza.
 */
@Controller
public class AuthWebController {

    /**
     * Executa l'operació `mostrarLogin`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    /**
     * Executa l'operació `mostrarAccesDenegat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/acces-denegat")
    public String mostrarAccesDenegat() {
        return "auth/acces-denegat";
    }
}
