package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO `RestablirContrasenyaDto` del projecte EasyTraza.
 */
public class RestablirContrasenyaDto {

    @NotBlank(message = "{recuperacio.token.obligatori}")
    private String token;

    @NotBlank(message = "{recuperacio.contrasenya.obligatoria}")
    @Size(max = 100, message = "{recuperacio.contrasenya.max}")
    private String novaContrasenya;

    @NotBlank(message = "{recuperacio.confirmacio.obligatoria}")
    @Size(max = 100, message = "{recuperacio.confirmacio.max}")
    private String confirmarContrasenya;

    /**
     * Crea una nova instància del component.
     */
    public RestablirContrasenyaDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param token paràmetre necessari per a l'operació.
     */
    public RestablirContrasenyaDto(String token) {
        this.token = token;
    }

    /**
     * Executa l'operació `getToken`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getToken() {
        return token;
    }

    /**
     * Executa l'operació `setToken`.
     *
     * @param token paràmetre necessari per a l'operació.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Executa l'operació `getNovaContrasenya`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getNovaContrasenya() {
        return novaContrasenya;
    }

    /**
     * Executa l'operació `setNovaContrasenya`.
     *
     * @param novaContrasenya paràmetre necessari per a l'operació.
     */
    public void setNovaContrasenya(String novaContrasenya) {
        this.novaContrasenya = novaContrasenya;
    }

    /**
     * Executa l'operació `getConfirmarContrasenya`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getConfirmarContrasenya() {
        return confirmarContrasenya;
    }

    /**
     * Executa l'operació `setConfirmarContrasenya`.
     *
     * @param confirmarContrasenya paràmetre necessari per a l'operació.
     */
    public void setConfirmarContrasenya(String confirmarContrasenya) {
        this.confirmarContrasenya = confirmarContrasenya;
    }
}
