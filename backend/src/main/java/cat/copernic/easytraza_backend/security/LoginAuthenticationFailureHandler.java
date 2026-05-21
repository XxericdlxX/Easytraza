package cat.copernic.easytraza_backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * Gestiona els intents fallits d'inici de sessió.
 *
 * <p>
 * El component registra l'error al log de seguretat i retorna l'usuari a la
 * pantalla de login amb l'indicador d'error corresponent.</p>
 */
@Component
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOGIN_LOGGER = LoggerFactory.getLogger("easytraza.login");

    /**
     * Registra un intent d'autenticació incorrecte i redirigeix al login.
     *
     * @param request petició HTTP original del formulari d'autenticació
     * @param response resposta HTTP utilitzada per fer la redirecció
     * @param exception excepció d'autenticació generada
     * @throws IOException si falla la redirecció
     * @throws ServletException si es produeix un error de processament servlet
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        LOGIN_LOGGER.warn("Intent d'inici de sessió fallit.");
        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}
