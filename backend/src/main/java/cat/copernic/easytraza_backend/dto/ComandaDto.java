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

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @Valid
    private List<LiniaComandaDto> linies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param id paràmetre necessari per executar l'operació.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public LocalDate getDataComanda() {
        return dataComanda;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param dataComanda paràmetre necessari per executar l'operació.
     */
    public void setDataComanda(LocalDate dataComanda) {
        this.dataComanda = dataComanda;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getClientNif() {
        return clientNif;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param clientNif paràmetre necessari per executar l'operació.
     */
    public void setClientNif(String clientNif) {
        this.clientNif = clientNif;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public EstatComanda getEstat() {
        return estat;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param estat paràmetre necessari per executar l'operació.
     */
    public void setEstat(EstatComanda estat) {
        this.estat = estat;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getObservacions() {
        return observacions;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param observacions paràmetre necessari per executar l'operació.
     */
    public void setObservacions(String observacions) {
        this.observacions = observacions;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getUsuariCreadorNom() {
        return usuariCreadorNom;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param usuariCreadorNom paràmetre necessari per executar l'operació.
     */
    public void setUsuariCreadorNom(String usuariCreadorNom) {
        this.usuariCreadorNom = usuariCreadorNom;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LiniaComandaDto> getLinies() {
        return linies;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param linies paràmetre necessari per executar l'operació.
     */
    public void setLinies(List<LiniaComandaDto> linies) {
        this.linies = linies;
    }
}
