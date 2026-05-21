package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO `LiniaClientDto` del projecte EasyTraza.
 */
public class LiniaClientDto {

    private Long id;

    private Integer numeroLotIntern;

    @NotNull(message = "{albara.client.linia.producte.obligatori}")
    private Long producteId;

    @NotNull(message = "{albara.client.linia.quantitat.obligatoria}")
    @Min(value = 1, message = "{albara.client.linia.quantitat.min}")
    private Integer quantitat;

    /**
     * Crea una nova instància del component.
     */
    public LiniaClientDto() {
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
     * Executa l'operació `getNumeroLotIntern`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Integer getNumeroLotIntern() {
        return numeroLotIntern;
    }

    /**
     * Executa l'operació `setNumeroLotIntern`.
     *
     * @param numeroLotIntern paràmetre necessari per a l'operació.
     */
    public void setNumeroLotIntern(Integer numeroLotIntern) {
        this.numeroLotIntern = numeroLotIntern;
    }

    /**
     * Executa l'operació `getProducteId`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Long getProducteId() {
        return producteId;
    }

    /**
     * Executa l'operació `setProducteId`.
     *
     * @param producteId paràmetre necessari per a l'operació.
     */
    public void setProducteId(Long producteId) {
        this.producteId = producteId;
    }

    /**
     * Executa l'operació `getQuantitat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Integer getQuantitat() {
        return quantitat;
    }

    /**
     * Executa l'operació `setQuantitat`.
     *
     * @param quantitat paràmetre necessari per a l'operació.
     */
    public void setQuantitat(Integer quantitat) {
        this.quantitat = quantitat;
    }
}
