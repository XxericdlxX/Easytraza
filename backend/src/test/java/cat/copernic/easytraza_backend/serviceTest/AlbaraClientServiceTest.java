package cat.copernic.easytraza_backend.serviceTest;

import cat.copernic.easytraza_backend.service.AlbaraClientService;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests bàsics del servei AlbaraClientService.
 */
class AlbaraClientServiceTest {

    @Test
    void serveiTeAnotacioService() {
        assertTrue(
                AlbaraClientService.class.isAnnotationPresent(Service.class),
                "AlbaraClientService ha de tenir @Service"
        );
    }

    @Test
    void serveiTeMetodesPublicsDeNegoci() {
        assertTrue(
                teAlgunMetodePublicDeNegoci(AlbaraClientService.class),
                "AlbaraClientService ha de tenir com a mínim un mètode públic de negoci"
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
