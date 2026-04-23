package cat.copernic.easytraza_backend.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlbaraProveidorDto {

    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataRecepcio;

    private String proveidorCif;

    private String usuariReceptorNom;

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

    public String getUsuariReceptorNom() {
        return usuariReceptorNom;
    }

    public void setUsuariReceptorNom(String usuariReceptorNom) {
        this.usuariReceptorNom = usuariReceptorNom;
    }

    public List<LotProveidorDto> getLots() {
        return lots;
    }

    public void setLots(List<LotProveidorDto> lots) {
        this.lots = lots;
    }
}
