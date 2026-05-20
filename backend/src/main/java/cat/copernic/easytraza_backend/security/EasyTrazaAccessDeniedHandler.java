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

@Component
public class EasyTrazaAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGIN_LOGGER = LoggerFactory.getLogger("easytraza.login");

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        LOGIN_LOGGER.warn("Accés denegat a una ruta restringida: {}", request.getRequestURI());
        response.sendRedirect(request.getContextPath() + "/acces-denegat");
    }
}
