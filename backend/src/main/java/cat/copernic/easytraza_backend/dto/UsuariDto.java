package cat.copernic.easytraza_backend.dto;

import cat.copernic.easytraza_backend.model.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO `UsuariDto` del projecte EasyTraza.
 */
public class UsuariDto {

    private Long id;

    @NotBlank(message = "{usuaris.nom.obligatori}")
    @Size(max = 50, message = "{usuaris.nom.max}")
    private String nom;

    @NotBlank(message = "{usuaris.cognoms.obligatoris}")
    @Size(max = 100, message = "{usuaris.cognoms.max}")
    private String cognoms;

    @NotNull(message = "{usuaris.rol.obligatori}")
    private Rol rol;

    @NotBlank(message = "{usuaris.email.obligatori}")
    @Email(message = "{usuaris.email.invalid}")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "{usuaris.email.pattern}"
    )
    private String email;

    @Size(max = 100, message = "{usuaris.contrasenya.max}")
    private String contrasenya;

    /**
     * Crea una nova instància del component.
     */
    public UsuariDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param cognoms paràmetre necessari per a l'operació.
     * @param rol paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @param contrasenya paràmetre necessari per a l'operació.
     */
    public UsuariDto(Long id, String nom, String cognoms, Rol rol, String email, String contrasenya) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.rol = rol;
        this.email = email;
        this.contrasenya = contrasenya;
    }

    /**
     * Executa l'operació `getId`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Long getId() {
        return id;
    }

    /**
     * Executa l'operació `setId`.
     *
     * @param id paràmetre necessari per a l'operació.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Executa l'operació `getNom`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Executa l'operació `setNom`.
     *
     * @param nom paràmetre necessari per a l'operació.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Executa l'operació `getCognoms`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getCognoms() {
        return cognoms;
    }

    /**
     * Executa l'operació `setCognoms`.
     *
     * @param cognoms paràmetre necessari per a l'operació.
     */
    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    /**
     * Executa l'operació `getRol`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Rol getRol() {
        return rol;
    }

    /**
     * Executa l'operació `setRol`.
     *
     * @param rol paràmetre necessari per a l'operació.
     */
    public void setRol(Rol rol) {
        this.rol = rol;
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

    /**
     * Executa l'operació `getContrasenya`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getContrasenya() {
        return contrasenya;
    }

    /**
     * Executa l'operació `setContrasenya`.
     *
     * @param contrasenya paràmetre necessari per a l'operació.
     */
    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }
}
