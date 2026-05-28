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

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
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
    public Long getProducteId() {
        return producteId;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param producteId paràmetre necessari per executar l'operació.
     */
    public void setProducteId(Long producteId) {
        this.producteId = producteId;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Integer getQuantitat() {
        return quantitat;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param quantitat paràmetre necessari per executar l'operació.
     */
    public void setQuantitat(Integer quantitat) {
        this.quantitat = quantitat;
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
}
