package cat.copernic.easytraza_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveidors")
public class Proveidor {

    @Id
    @Column(nullable = false, unique = true, length = 20)
    private String cif;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String adreca;

    @Column(length = 255)
    private String notes;

    @Column(length = 20)
    private String telefon;

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
