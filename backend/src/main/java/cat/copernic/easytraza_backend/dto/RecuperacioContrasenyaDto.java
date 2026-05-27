package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO `RecuperacioContrasenyaDto` del projecte EasyTraza.
 */
public class RecuperacioContrasenyaDto {

    @NotBlank(message = "{recuperacio.email.obligatori}")
    @Email(message = "{recuperacio.email.invalid}")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "{recuperacio.email.pattern}"
    )
    @Size(max = 100, message = "{recuperacio.email.max}")
    private String email;

    /**
     * Crea una nova instància del component.
     */
    public RecuperacioContrasenyaDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param email paràmetre necessari per a l'operació.
     */
    public RecuperacioContrasenyaDto(String email) {
        this.email = email;
    }

    /**
     * Executa l'operació `getEmail`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Executa l'operació `setEmail`.
     *
     * @param email paràmetre necessari per a l'operació.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
