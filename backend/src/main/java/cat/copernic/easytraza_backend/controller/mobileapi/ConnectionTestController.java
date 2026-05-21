package cat.copernic.easytraza_backend.controller.mobileapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador de l’API mobile `ConnectionTestController` del projecte
 * EasyTraza.
 */
@RestController
public class ConnectionTestController {

    /**
     * Executa l'operació `testConnection`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping("/api/test-connection")
    public Map<String, String> testConnection() {
        return Map.of("message", "Backend EasyTraza actiu");
    }
}
