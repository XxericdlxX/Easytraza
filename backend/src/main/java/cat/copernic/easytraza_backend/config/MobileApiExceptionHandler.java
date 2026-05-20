package cat.copernic.easytraza_backend.config;

import cat.copernic.easytraza_backend.dto.mobile.MobileApiErrorDto;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "cat.copernic.easytraza_backend.controller.mobileapi")
public class MobileApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MobileApiExceptionHandler.class);

    private final MessageSource messageSource;

    public MobileApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MobileApiErrorDto> gestionarPeticioIncorrecta(
            IllegalArgumentException exception, Locale locale) {
        LOGGER.warn("Petició mobile incorrecta: {}", exception.getMessage());
        return ResponseEntity.badRequest().body(
                new MobileApiErrorDto(
                        "mobile.error.peticio",
                        missatgeSegur(exception.getMessage(), "error.validacio", locale)
                )
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<MobileApiErrorDto> gestionarEstatIncorrecte(
            IllegalStateException exception, Locale locale) {
        LOGGER.warn("Estat mobile no permès: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new MobileApiErrorDto(
                        "mobile.error.estat",
                        missatgeSegur(exception.getMessage(), "error.generic", locale)
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MobileApiErrorDto> gestionarErrorGeneric(Exception exception, Locale locale) {
        LOGGER.error("Error no controlat en una petició mobile", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new MobileApiErrorDto(
                        "mobile.error.generic",
                        missatgeSegur("error.generic", "error.generic", locale)
                )
        );
    }

    private String missatgeSegur(String codi, String codiPerDefecte, Locale locale) {
        String fallback = messageSource.getMessage(codiPerDefecte, null, codiPerDefecte, locale);
        return messageSource.getMessage(codi, null, fallback, locale);
    }
}
