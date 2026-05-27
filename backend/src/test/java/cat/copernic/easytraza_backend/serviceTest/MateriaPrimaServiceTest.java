package cat.copernic.easytraza_backend.serviceTest;

import cat.copernic.easytraza_backend.service.MateriaPrimaService;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests bàsics del servei MateriaPrimaService.
 */
class MateriaPrimaServiceTest {

    @Test
    void serveiTeAnotacioService() {
        assertTrue(
                MateriaPrimaService.class.isAnnotationPresent(Service.class),
                "MateriaPrimaService ha de tenir @Service"
        );
    }

    @Test
    void serveiTeMetodesPublicsDeNegoci() {
        assertTrue(
                teAlgunMetodePublicDeNegoci(MateriaPrimaService.class),
                "MateriaPrimaService ha de tenir com a mínim un mètode públic de negoci"
        );
    }

    private boolean teAlgunMetodePublicDeNegoci(Class<?> tipus) {
        for (Method metode : tipus.getDeclaredMethods()) {
            if (Modifier.isPublic(metode.getModifiers())
                    && !metode.isSynthetic()
                    && !metode.getName().startsWith("lambda$")) {
                return true;
            }
        }
        return false;
    }
}
