package cat.copernic.easytraza_backend.dto;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO `AlbaraClientDto` del projecte EasyTraza.
 */
public class AlbaraClientDto {

    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataProduccio;

    private String clientNif;

    private String usuariCreadorNom;

    @Valid
    private List<LiniaClientDto> linies = new ArrayList<>();

    /**
     * Crea una nova instància del component.
     */
    public AlbaraClientDto() {
    }

    /**
     * Executa l'operació `getId`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Long getId() {
        return id;
    }

    /**
     * Executa l'operació `setId`.
     *
     * @param id paràmetre necessari per a l'operació.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Executa l'operació `getDataProduccio`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public LocalDate getDataProduccio() {
        return dataProduccio;
    }

    /**
     * Executa l'operació `setDataProduccio`.
     *
     * @param dataProduccio paràmetre necessari per a l'operació.
     */
    public void setDataProduccio(LocalDate dataProduccio) {
        this.dataProduccio = dataProduccio;
    }

    /**
     * Executa l'operació `getClientNif`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getClientNif() {
        return clientNif;
    }

    /**
     * Executa l'operació `setClientNif`.
     *
     * @param clientNif paràmetre necessari per a l'operació.
     */
    public void setClientNif(String clientNif) {
        this.clientNif = clientNif;
    }

    /**
     * Executa l'operació `getUsuariCreadorNom`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getUsuariCreadorNom() {
        return usuariCreadorNom;
    }

    /**
     * Executa l'operació `setUsuariCreadorNom`.
     *
     * @param usuariCreadorNom paràmetre necessari per a l'operació.
     */
    public void setUsuariCreadorNom(String usuariCreadorNom) {
        this.usuariCreadorNom = usuariCreadorNom;
    }

    /**
     * Executa l'operació `getLinies`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LiniaClientDto> getLinies() {
        return linies;
    }

    /**
     * Executa l'operació `setLinies`.
     *
     * @param linies paràmetre necessari per a l'operació.
     */
    public void setLinies(List<LiniaClientDto> linies) {
        this.linies = linies;
    }
}
