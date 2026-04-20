package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LotProveidorDto {

    private Long id;

    @NotBlank(message = "{lot.proveidor.codi.obligatori}")
    @Size(max = 100, message = "{lot.proveidor.codi.max}")
    private String codiLot;

    @NotNull(message = "{lot.proveidor.quantitat.obligatoria}")
    @Min(value = 1, message = "{lot.proveidor.quantitat.min}")
    private Integer quantitat;

    @NotNull(message = "{lot.proveidor.materia.obligatoria}")
    private Long materiaPrimaId;

    public LotProveidorDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodiLot() {
        return codiLot;
    }

    public void setCodiLot(String codiLot) {
        this.codiLot = codiLot;
    }

    public Integer getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Integer quantitat) {
        this.quantitat = quantitat;
    }

    public Long getMateriaPrimaId() {
        return materiaPrimaId;
    }

    public void setMateriaPrimaId(Long materiaPrimaId) {
        this.materiaPrimaId = materiaPrimaId;
    }
}
