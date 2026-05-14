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

    @NotBlank(message = "{perfil.email.obligatori}")
    @Email(message = "{perfil.email.invalid}")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "{perfil.email.pattern}"
    )
    @Size(max = 100, message = "{perfil.email.max}")
    private String email;

    public PerfilUsuariDto() {
    }

    public PerfilUsuariDto(Long id, String nom, String cognoms, String email) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
