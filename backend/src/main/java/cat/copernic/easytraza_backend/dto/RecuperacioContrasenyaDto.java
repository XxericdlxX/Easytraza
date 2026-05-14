package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RecuperacioContrasenyaDto {

    @NotBlank(message = "{recuperacio.email.obligatori}")
    @Email(message = "{recuperacio.email.invalid}")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "{recuperacio.email.pattern}"
    )
    @Size(max = 100, message = "{recuperacio.email.max}")
    private String email;

    public RecuperacioContrasenyaDto() {
    }

    public RecuperacioContrasenyaDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
