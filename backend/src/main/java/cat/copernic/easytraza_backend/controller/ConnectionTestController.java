package cat.copernic.easytraza_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConnectionTestController {

    @GetMapping("/api/test-connection")
    public Map<String, String> testConnection() {
        return Map.of("message", "Backend EasyTraza actiu");
    }
}
