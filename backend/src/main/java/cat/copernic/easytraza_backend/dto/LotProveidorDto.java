package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LotProveidorDto {

    private Long id;

    @NotBlank(message = "{lot.proveidor.codi.obligatori}")
    @Size(max = 100, message = "{lot.proveidor.codi.max}")
    private String codiLot;

    @NotNull(message = "{lot.proveidor.quantitat.obligatoria}")
    @DecimalMin(value = "0.01", message = "{lot.proveidor.quantitat.min}")
    private Double quantitat;

    @NotNull(message = "{lot.proveidor.materia.obligatoria}")
    private Long materiaPrimaId;

    private String materiaPrimaNomDetectada;

    private String codiMateriaPrimaOcr;

    private boolean crearMateriaPrimaSiNoExisteix;

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

    public Double getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Double quantitat) {
        this.quantitat = quantitat;
    }

    public Long getMateriaPrimaId() {
        return materiaPrimaId;
    }

    public void setMateriaPrimaId(Long materiaPrimaId) {
        this.materiaPrimaId = materiaPrimaId;
    }

    public String getMateriaPrimaNomDetectada() {
        return materiaPrimaNomDetectada;
    }

    public void setMateriaPrimaNomDetectada(String materiaPrimaNomDetectada) {
        this.materiaPrimaNomDetectada = materiaPrimaNomDetectada;
    }

    public String getCodiMateriaPrimaOcr() {
        return codiMateriaPrimaOcr;
    }

    public void setCodiMateriaPrimaOcr(String codiMateriaPrimaOcr) {
        this.codiMateriaPrimaOcr = codiMateriaPrimaOcr;
    }

    public boolean isCrearMateriaPrimaSiNoExisteix() {
        return crearMateriaPrimaSiNoExisteix;
    }

    public void setCrearMateriaPrimaSiNoExisteix(boolean crearMateriaPrimaSiNoExisteix) {
        this.crearMateriaPrimaSiNoExisteix = crearMateriaPrimaSiNoExisteix;
    }
}
