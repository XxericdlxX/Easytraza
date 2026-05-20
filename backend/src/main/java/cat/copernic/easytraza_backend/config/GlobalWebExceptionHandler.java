package cat.copernic.easytraza_backend.config;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice(annotations = Controller.class)
public class GlobalWebExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalWebExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalWebExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    public String gestionarErrorWeb(Exception exception, Locale locale, Model model, HttpServletResponse response) {
        LOGGER.error("Error no controlat en una petició web", exception);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute(
                "missatgeError",
                messageSource.getMessage("error.generic", null, "S'ha produït un error inesperat.", locale)
        );
        return "error/error-generico";
    }
}
