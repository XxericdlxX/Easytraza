package cat.copernic.easytraza_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "proveidors")
public class Proveidor {

    @Id
    @NotBlank(message = "{proveidors.cif.obligatori}")
    @Size(max = 20, message = "{proveidors.cif.max}")
    @Column(nullable = false, unique = true, length = 20)
    private String cif;

    @NotBlank(message = "{proveidors.nom.obligatori}")
    @Size(max = 100, message = "{proveidors.nom.max}")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "{proveidors.adreca.obligatoria}")
    @Size(max = 255, message = "{proveidors.adreca.max}")
    @Column(nullable = false, length = 255)
    private String adreca;

    @Size(max = 255, message = "{proveidors.notes.max}")
    @Column(length = 255)
    private String notes;

    @Size(max = 20, message = "{proveidors.telefon.max}")
    @Pattern(regexp = "^$|^[+0-9 ()-]{7,20}$", message = "{proveidors.telefon.invalid}")
    @Column(length = 20)
    private String telefon;

    @Size(max = 100, message = "{proveidors.email.max}")
    @Email(message = "{proveidors.email.invalid}")
    @Column(length = 100)
    private String email;

    public Proveidor() {
    }

    public Proveidor(String cif, String nom, String adreca, String notes, String telefon, String email) {
        this.cif = cif;
        this.nom = nom;
        this.adreca = adreca;
        this.notes = notes;
        this.telefon = telefon;
        this.email = email;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
