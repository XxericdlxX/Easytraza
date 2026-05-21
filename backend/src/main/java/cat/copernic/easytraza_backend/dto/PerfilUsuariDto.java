package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO `PerfilUsuariDto` del projecte EasyTraza.
 */
public class PerfilUsuariDto {

    private Long id;

    @NotBlank(message = "{perfil.nom.obligatori}")
    @Size(max = 50, message = "{perfil.nom.max}")
    private String nom;

    @NotBlank(message = "{perfil.cognoms.obligatoris}")
    @Size(max = 100, message = "{perfil.cognoms.max}")
    private String cognoms;

    @NotBlank(message = "{perfil.nif.obligatori}")
    @Size(max = 20, message = "{perfil.nif.max}")
    private String nif;

    @Size(max = 255, message = "{perfil.foto.max}")
    private String fotoPerfilNom;

    @NotBlank(message = "{perfil.email.obligatori}")
    @Email(message = "{perfil.email.invalid}")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "{perfil.email.pattern}"
    )
    @Size(max = 100, message = "{perfil.email.max}")
    private String email;

    @Size(max = 100, message = "{perfil.contrasenya.actual.max}")
    private String contrasenyaActual;

    @Size(max = 100, message = "{perfil.contrasenya.nova.max}")
    private String novaContrasenya;

    @Size(max = 100, message = "{perfil.contrasenya.confirmar.max}")
    private String confirmarContrasenya;

    /**
     * Crea una nova instància del component.
     */
    public PerfilUsuariDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param cognoms paràmetre necessari per a l'operació.
     * @param nif paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @param fotoPerfilNom paràmetre necessari per a l'operació.
     */
    public PerfilUsuariDto(Long id, String nom, String cognoms, String nif, String email, String fotoPerfilNom) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.nif = nif;
        this.email = email;
        this.fotoPerfilNom = fotoPerfilNom;
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
     * Executa l'operació `getNif`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getNif() {
        return nif;
    }

    /**
     * Executa l'operació `setNif`.
     *
     * @param nif paràmetre necessari per a l'operació.
     */
    public void setNif(String nif) {
        this.nif = nif;
    }

    /**
     * Executa l'operació `getFotoPerfilNom`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getFotoPerfilNom() {
        return fotoPerfilNom;
    }

    /**
     * Executa l'operació `setFotoPerfilNom`.
     *
     * @param fotoPerfilNom paràmetre necessari per a l'operació.
     */
    public void setFotoPerfilNom(String fotoPerfilNom) {
        this.fotoPerfilNom = fotoPerfilNom;
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
     * Executa l'operació `getContrasenyaActual`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getContrasenyaActual() {
        return contrasenyaActual;
    }

    /**
     * Executa l'operació `setContrasenyaActual`.
     *
     * @param contrasenyaActual paràmetre necessari per a l'operació.
     */
    public void setContrasenyaActual(String contrasenyaActual) {
        this.contrasenyaActual = contrasenyaActual;
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
