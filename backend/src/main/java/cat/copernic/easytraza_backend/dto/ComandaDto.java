package cat.copernic.easytraza_backend.dto;

import cat.copernic.easytraza_backend.model.enums.EstatComanda;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO per representar una comanda en els formularis web.
 */
public class ComandaDto {

    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataComanda;

    private String clientNif;

    private EstatComanda estat = EstatComanda.PENDENT;

    private String observacions;

    private String usuariCreadorNom;

    @Valid
    private List<LiniaComandaDto> linies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataComanda() {
        return dataComanda;
    }

    public void setDataComanda(LocalDate dataComanda) {
        this.dataComanda = dataComanda;
    }

    public String getClientNif() {
        return clientNif;
    }

    public void setClientNif(String clientNif) {
        this.clientNif = clientNif;
    }

    public EstatComanda getEstat() {
        return estat;
    }

    public void setEstat(EstatComanda estat) {
        this.estat = estat;
    }

    public String getObservacions() {
        return observacions;
    }

    public void setObservacions(String observacions) {
        this.observacions = observacions;
    }

    public String getUsuariCreadorNom() {
        return usuariCreadorNom;
    }

    public void setUsuariCreadorNom(String usuariCreadorNom) {
        this.usuariCreadorNom = usuariCreadorNom;
    }

    public List<LiniaComandaDto> getLinies() {
        return linies;
    }

    public void setLinies(List<LiniaComandaDto> linies) {
        this.linies = linies;
    }
}
