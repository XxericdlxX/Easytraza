package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlbaraProveidorDto {

    private Long id;

    @NotNull(message = "{albara.proveidor.data.obligatoria}")
    private LocalDate dataRecepcio;

    @NotNull(message = "{albara.proveidor.proveidor.obligatori}")
    private String proveidorCif;

    private List<LotProveidorDto> lots = new ArrayList<>();

    public AlbaraProveidorDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataRecepcio() {
        return dataRecepcio;
    }

    public void setDataRecepcio(LocalDate dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    public String getProveidorCif() {
        return proveidorCif;
    }

    public void setProveidorCif(String proveidorCif) {
        this.proveidorCif = proveidorCif;
    }

    public List<LotProveidorDto> getLots() {
        return lots;
    }

    public void setLots(List<LotProveidorDto> lots) {
        this.lots = lots;
    }
}
