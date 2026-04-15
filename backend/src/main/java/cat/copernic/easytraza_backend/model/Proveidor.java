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

    @Column(nullable = false)
    private String descripcio;

    public Proveidor() {
    }

    public Proveidor(String cif, String nom, String adreca, String descripcio) {
        this.cif = cif;
        this.nom = nom;
        this.adreca = adreca;
        this.descripcio = descripcio;
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

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
}
