package cat.copernic.easytraza_backend.dto;

import cat.copernic.easytraza_backend.model.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuariDto {

    private Long id;

    @NotBlank(message = "El nom és obligatori")
    @Size(max = 50, message = "El nom no pot tenir més de 50 caràcters")
    private String nom;

    @NotBlank(message = "Els cognoms són obligatoris")
    @Size(max = 100, message = "Els cognoms no poden tenir més de 100 caràcters")
    private String cognoms;

    @NotNull(message = "El rol és obligatori")
    private Rol rol;

    @NotBlank(message = "El correu electrònic és obligatori")
    @Email(message = "El correu electrònic ha de tenir un format vàlid")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "El correu electrònic ha de tenir un format tipus usuari@domini.com"
    )
    private String email;

    @Size(max = 100, message = "La contrasenya no pot tenir més de 100 caràcters")
    private String contrasenya;

    public UsuariDto() {
    }

    public UsuariDto(Long id, String nom, String cognoms, Rol rol, String email, String contrasenya) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.rol = rol;
        this.email = email;
        this.contrasenya = contrasenya;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }
}
