package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.TipusClient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @Column(nullable = false, unique = true, length = 20)
    private String nif;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String cognoms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipusClient tipusClient;

    @Column(nullable = false)
    private String adreca;

    @Column(length = 50)
    private String rgs;

    @Column(length = 20)
    private String telefon;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String notes;

    public Client() {
    }

    public Client(String nif, String nom, String cognoms, TipusClient tipusClient,
            String adreca, String rgs, String telefon, String email, String notes) {
        this.nif = nif;
        this.nom = nom;
        this.cognoms = cognoms;
        this.tipusClient = tipusClient;
        this.adreca = adreca;
        this.rgs = rgs;
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

    public String getCognoms() {
        return cognoms;
    }

    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
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

    public String getRgs() {
        return rgs;
    }

    public void setRgs(String rgs) {
        this.rgs = rgs;
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
