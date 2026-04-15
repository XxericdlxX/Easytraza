package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProveidorDto {

    @NotBlank(message = "{proveidors.cif.obligatori}")
    @Size(max = 20, message = "{proveidors.cif.max}")
    private String cif;

    @NotBlank(message = "{proveidors.nom.obligatori}")
    @Size(max = 100, message = "{proveidors.nom.max}")
    private String nom;

    @NotBlank(message = "{proveidors.adreca.obligatoria}")
    @Size(max = 255, message = "{proveidors.adreca.max}")
    private String adreca;

    @NotBlank(message = "{proveidors.descripcio.obligatoria}")
    @Size(max = 255, message = "{proveidors.descripcio.max}")
    private String descripcio;

    public ProveidorDto() {
    }

    public ProveidorDto(String cif, String nom, String adreca, String descripcio) {
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
