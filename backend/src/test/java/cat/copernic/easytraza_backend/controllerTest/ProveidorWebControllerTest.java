package cat.copernic.easytraza_backend.controllerTest;

import cat.copernic.easytraza_backend.controller.ProveidorWebController;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests bàsics del controlador ProveidorWebController.
 */
class ProveidorWebControllerTest {

    @Test
    void controladorTeAnotacioSpringCorrecta() {
        assertTrue(
                ProveidorWebController.class.isAnnotationPresent(Controller.class),
                "ProveidorWebController ha de tenir @Controller"
        );
    }

    @Test
    void controladorDefineixAlgunaRuta() {
        assertTrue(
                teMappingDeClasse(ProveidorWebController.class) || teAlgunMetodeAmbMapping(ProveidorWebController.class),
                "ProveidorWebController ha de definir rutes amb annotations de Spring MVC"
        );
    }

    private boolean teMappingDeClasse(Class<?> tipus) {
        return tipus.isAnnotationPresent(RequestMapping.class);
    }

    private boolean teAlgunMetodeAmbMapping(Class<?> tipus) {
        for (Method metode : tipus.getDeclaredMethods()) {
            if (metode.isAnnotationPresent(GetMapping.class)
                    || metode.isAnnotationPresent(PostMapping.class)
                    || metode.isAnnotationPresent(PutMapping.class)
                    || metode.isAnnotationPresent(DeleteMapping.class)
                    || metode.isAnnotationPresent(PatchMapping.class)
                    || metode.isAnnotationPresent(RequestMapping.class)) {
                return true;
            }
        }
        return false;
    }
}
