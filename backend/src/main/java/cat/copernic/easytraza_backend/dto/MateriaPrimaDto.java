package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MateriaPrimaDto {

    private Long id;

    @NotBlank(message = "{materies.nom.obligatori}")
    @Size(max = 100, message = "{materies.nom.max}")
    private String nom;

    @NotBlank(message = "{materies.descripcio.obligatoria}")
    @Size(max = 255, message = "{materies.descripcio.max}")
    private String descripcio;

    public MateriaPrimaDto() {
    }

    public MateriaPrimaDto(Long id, String nom, String descripcio) {
        this.id = id;
        this.nom = nom;
        this.descripcio = descripcio;
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

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
}
