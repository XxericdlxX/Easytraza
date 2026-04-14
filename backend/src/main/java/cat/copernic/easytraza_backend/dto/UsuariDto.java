package cat.copernic.easytraza_backend.dto;

import cat.copernic.easytraza_backend.model.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuariDto {

    private Long id;

    @NotBlank(message = "El nom és obligatori")
    private String nom;

    @NotBlank(message = "Els cognoms són obligatoris")
    private String cognoms;

    private Rol rol;

    @NotBlank(message = "El correu electrònic és obligatori")
    @Email(message = "El correu electrònic no té un format vàlid")
    private String email;

    @Size(max = 100, message = "La contrasenya no pot superar els 100 caràcters")
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
