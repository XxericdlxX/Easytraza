package cat.copernic.easytraza_backend.controllerTest;

import cat.copernic.easytraza_backend.controller.PerfilWebController;
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
 * Tests bàsics del controlador PerfilWebController.
 */
class PerfilWebControllerTest {

    @Test
    void controladorTeAnotacioSpringCorrecta() {
        assertTrue(
                PerfilWebController.class.isAnnotationPresent(Controller.class),
                "PerfilWebController ha de tenir @Controller"
        );
    }

    @Test
    void controladorDefineixAlgunaRuta() {
        assertTrue(
                teMappingDeClasse(PerfilWebController.class) || teAlgunMetodeAmbMapping(PerfilWebController.class),
                "PerfilWebController ha de definir rutes amb annotations de Spring MVC"
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
