package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

    public PerfilUsuariDto() {
    }

    public PerfilUsuariDto(Long id, String nom, String cognoms, String nif, String email, String fotoPerfilNom) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.nif = nif;
        this.email = email;
        this.fotoPerfilNom = fotoPerfilNom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognoms() {
        return cognoms;
    }

    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getFotoPerfilNom() {
        return fotoPerfilNom;
    }

    public void setFotoPerfilNom(String fotoPerfilNom) {
        this.fotoPerfilNom = fotoPerfilNom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenyaActual() {
        return contrasenyaActual;
    }

    public void setContrasenyaActual(String contrasenyaActual) {
        this.contrasenyaActual = contrasenyaActual;
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
