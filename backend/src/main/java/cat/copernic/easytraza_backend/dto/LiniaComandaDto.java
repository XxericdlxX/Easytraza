package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO per representar una línia de comanda en els formularis web.
 */
public class LiniaComandaDto {

    private Long id;

    @NotNull(message = "{comandes.linia.producte.obligatori}")
    private Long producteId;

    @NotNull(message = "{comandes.linia.quantitat.obligatoria}")
    @Min(value = 1, message = "{comandes.linia.quantitat.min}")
    private Integer quantitat;

    private String observacions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProducteId() {
        return producteId;
    }

    public void setProducteId(Long producteId) {
        this.producteId = producteId;
    }

    public Integer getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Integer quantitat) {
        this.quantitat = quantitat;
    }

    public String getObservacions() {
        return observacions;
    }

    public void setObservacions(String observacions) {
        this.observacions = observacions;
    }
}
