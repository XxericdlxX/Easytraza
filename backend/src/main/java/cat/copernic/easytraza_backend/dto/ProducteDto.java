package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProducteDto {

    private Long id;

    @NotBlank(message = "{productes.descripcio.obligatoria}")
    @Size(max = 255, message = "{productes.descripcio.max}")
    private String descripcio;

    public ProducteDto() {
    }

    public ProducteDto(Long id, String descripcio) {
        this.id = id;
        this.descripcio = descripcio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
}
