package cat.copernic.easytraza_backend.dto;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class AlbaraClientDto {

    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataProduccio;

    private String clientNif;

    private String usuariCreadorNom;

    @Valid
    private List<LiniaClientDto> linies = new ArrayList<>();

    public AlbaraClientDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataProduccio() {
        return dataProduccio;
    }

    public void setDataProduccio(LocalDate dataProduccio) {
        this.dataProduccio = dataProduccio;
    }

    public String getClientNif() {
        return clientNif;
    }

    public void setClientNif(String clientNif) {
        this.clientNif = clientNif;
    }

    public String getUsuariCreadorNom() {
        return usuariCreadorNom;
    }

    public void setUsuariCreadorNom(String usuariCreadorNom) {
        this.usuariCreadorNom = usuariCreadorNom;
    }

    public List<LiniaClientDto> getLinies() {
        return linies;
    }

    public void setLinies(List<LiniaClientDto> linies) {
        this.linies = linies;
    }
}
