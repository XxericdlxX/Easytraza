package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LiniaClientDto {

    private Long id;

    private Integer numeroLotIntern;

    @NotNull(message = "{albara.client.linia.producte.obligatori}")
    private Long producteId;

    @NotNull(message = "{albara.client.linia.quantitat.obligatoria}")
    @Min(value = 1, message = "{albara.client.linia.quantitat.min}")
    private Integer quantitat;

    public LiniaClientDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroLotIntern() {
        return numeroLotIntern;
    }

    public void setNumeroLotIntern(Integer numeroLotIntern) {
        this.numeroLotIntern = numeroLotIntern;
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
}
