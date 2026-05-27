package cat.copernic.easytraza_backend.serviceTest;

import cat.copernic.easytraza_backend.service.AlbaraProveidorService;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests bàsics del servei AlbaraProveidorService.
 */
class AlbaraProveidorServiceTest {

    @Test
    void serveiTeAnotacioService() {
        assertTrue(
                AlbaraProveidorService.class.isAnnotationPresent(Service.class),
                "AlbaraProveidorService ha de tenir @Service"
        );
    }

    @Test
    void serveiTeMetodesPublicsDeNegoci() {
        assertTrue(
                teAlgunMetodePublicDeNegoci(AlbaraProveidorService.class),
                "AlbaraProveidorService ha de tenir com a mínim un mètode públic de negoci"
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
