package cat.copernic.easytraza_backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Gestiona els accessos denegats a rutes web protegides.
 *
 * <p>
 * Quan un usuari autenticat intenta accedir a una URL per a la qual no té
 * permisos, registra l'incident al log de seguretat i el redirigeix a la
 * pantalla d'accés denegat.</p>
 */
@Component
public class EasyTrazaAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGIN_LOGGER = LoggerFactory.getLogger("easytraza.login");

    /**
     * Processa un accés denegat i redirigeix l'usuari a la vista informativa.
     *
     * @param request petició HTTP que ha intentat accedir a la ruta restringida
     * @param response resposta HTTP que es redirigirà
     * @param accessDeniedException excepció generada per Spring Security
     * @throws IOException si falla la redirecció de la resposta
     * @throws ServletException si es produeix un error de processament servlet
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        LOGIN_LOGGER.warn("Accés denegat a una ruta restringida: {}", request.getRequestURI());
        response.sendRedirect(request.getContextPath() + "/acces-denegat");
    }
}
