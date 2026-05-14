package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.TipusClient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @NotBlank(message = "{clients.nif.obligatori}")
    @Size(max = 20, message = "{clients.nif.max}")
    @Column(nullable = false, unique = true, length = 20)
    private String nif;

    @NotBlank(message = "{clients.nom.obligatori}")
    @Size(max = 100, message = "{clients.nom.max}")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotNull(message = "{clients.tipus.obligatori}")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipusClient tipusClient;

    @Size(max = 100, message = "{clients.tipus.altres.max}")
    @Column(length = 100)
    private String tipusClientAltres;

    @NotBlank(message = "{clients.adreca.obligatoria}")
    @Size(max = 255, message = "{clients.adreca.max}")
    @Column(nullable = false, length = 255)
    private String adreca;

    @Size(max = 20, message = "{clients.telefon.max}")
    @Pattern(regexp = "^$|^[+0-9 ()-]{7,20}$", message = "{clients.telefon.invalid}")
    @Column(length = 20)
    private String telefon;

    @Size(max = 100, message = "{clients.email.max}")
    @Email(message = "{clients.email.invalid}")
    @Column(length = 100)
    private String email;

    @Size(max = 255, message = "{clients.notes.max}")
    @Column(length = 255)
    private String notes;

    public Client() {
    }

    public Client(String nif, String nom, TipusClient tipusClient,
            String adreca, String telefon, String email, String notes) {
        this.nif = nif;
        this.nom = nom;
        this.tipusClient = tipusClient;
        this.adreca = adreca;
        this.telefon = telefon;
        this.email = email;
        this.notes = notes;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TipusClient getTipusClient() {
        return tipusClient;
    }

    public void setTipusClient(TipusClient tipusClient) {
        this.tipusClient = tipusClient;
    }

    public String getTipusClientAltres() {
        return tipusClientAltres;
    }

    public void setTipusClientAltres(String tipusClientAltres) {
        this.tipusClientAltres = tipusClientAltres;
    }

    public String getAdreca() {
        return adreca;
    }

    public void setAdreca(String adreca) {
        this.adreca = adreca;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
