package cat.copernic.easytraza_backend.dto;

import cat.copernic.easytraza_backend.model.enums.TipusClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClientDto {

    @NotBlank(message = "{clients.nif.obligatori}")
    @Size(max = 20, message = "{clients.nif.max}")
    private String nif;

    @NotBlank(message = "{clients.nom.obligatori}")
    @Size(max = 100, message = "{clients.nom.max}")
    private String nom;

    @NotNull(message = "{clients.tipus.obligatori}")
    private TipusClient tipusClient;

    @NotBlank(message = "{clients.adreca.obligatoria}")
    @Size(max = 255, message = "{clients.adreca.max}")
    private String adreca;

    @Size(max = 20, message = "{clients.telefon.max}")
    @Pattern(
            regexp = "^$|^[+0-9 ()-]{7,20}$",
            message = "{clients.telefon.invalid}"
    )
    private String telefon;

    @Size(max = 100, message = "{clients.email.max}")
    @Email(message = "{clients.email.invalid}")
    private String email;

    @Size(max = 255, message = "{clients.notes.max}")
    private String notes;

    public ClientDto() {
    }

    public ClientDto(String nif, String nom, TipusClient tipusClient,
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
