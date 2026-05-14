package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RestablirContrasenyaDto {

    @NotBlank(message = "{recuperacio.token.obligatori}")
    private String token;

    @NotBlank(message = "{recuperacio.contrasenya.obligatoria}")
    @Size(max = 100, message = "{recuperacio.contrasenya.max}")
    private String novaContrasenya;

    @NotBlank(message = "{recuperacio.confirmacio.obligatoria}")
    @Size(max = 100, message = "{recuperacio.confirmacio.max}")
    private String confirmarContrasenya;

    public RestablirContrasenyaDto() {
    }

    public RestablirContrasenyaDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNovaContrasenya() {
        return novaContrasenya;
    }

    public void setNovaContrasenya(String novaContrasenya) {
        this.novaContrasenya = novaContrasenya;
    }

    public String getConfirmarContrasenya() {
        return confirmarContrasenya;
    }

    public void setConfirmarContrasenya(String confirmarContrasenya) {
        this.confirmarContrasenya = confirmarContrasenya;
    }
}
