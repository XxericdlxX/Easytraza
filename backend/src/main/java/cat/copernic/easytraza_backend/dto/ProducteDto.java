package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProducteDto {

    private Long id;

    @NotBlank(message = "{productes.nom.obligatori}")
    @Size(max = 100, message = "{productes.nom.max}")
    private String nom;

    @NotBlank(message = "{productes.descripcio.obligatoria}")
    @Size(max = 255, message = "{productes.descripcio.max}")
    private String descripcio;

    public ProducteDto() {
    }

    public ProducteDto(Long id, String nom, String descripcio) {
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
