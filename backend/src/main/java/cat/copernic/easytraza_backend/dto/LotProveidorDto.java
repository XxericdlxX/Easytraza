package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO `LotProveidorDto` del projecte EasyTraza.
 */
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

    /**
     * Crea una nova instància del component.
     */
    public LotProveidorDto() {
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
     * Executa l'operació `getCodiLot`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getCodiLot() {
        return codiLot;
    }

    /**
     * Executa l'operació `setCodiLot`.
     *
     * @param codiLot paràmetre necessari per a l'operació.
     */
    public void setCodiLot(String codiLot) {
        this.codiLot = codiLot;
    }

    /**
     * Executa l'operació `getQuantitat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Double getQuantitat() {
        return quantitat;
    }

    /**
     * Executa l'operació `setQuantitat`.
     *
     * @param quantitat paràmetre necessari per a l'operació.
     */
    public void setQuantitat(Double quantitat) {
        this.quantitat = quantitat;
    }

    /**
     * Executa l'operació `getMateriaPrimaId`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Long getMateriaPrimaId() {
        return materiaPrimaId;
    }

    /**
     * Executa l'operació `setMateriaPrimaId`.
     *
     * @param materiaPrimaId paràmetre necessari per a l'operació.
     */
    public void setMateriaPrimaId(Long materiaPrimaId) {
        this.materiaPrimaId = materiaPrimaId;
    }

    /**
     * Executa l'operació `getMateriaPrimaNomDetectada`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getMateriaPrimaNomDetectada() {
        return materiaPrimaNomDetectada;
    }

    /**
     * Executa l'operació `setMateriaPrimaNomDetectada`.
     *
     * @param materiaPrimaNomDetectada paràmetre necessari per a l'operació.
     */
    public void setMateriaPrimaNomDetectada(String materiaPrimaNomDetectada) {
        this.materiaPrimaNomDetectada = materiaPrimaNomDetectada;
    }

    /**
     * Executa l'operació `getCodiMateriaPrimaOcr`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getCodiMateriaPrimaOcr() {
        return codiMateriaPrimaOcr;
    }

    /**
     * Executa l'operació `setCodiMateriaPrimaOcr`.
     *
     * @param codiMateriaPrimaOcr paràmetre necessari per a l'operació.
     */
    public void setCodiMateriaPrimaOcr(String codiMateriaPrimaOcr) {
        this.codiMateriaPrimaOcr = codiMateriaPrimaOcr;
    }

    /**
     * Executa l'operació `isCrearMateriaPrimaSiNoExisteix`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean isCrearMateriaPrimaSiNoExisteix() {
        return crearMateriaPrimaSiNoExisteix;
    }

    /**
     * Executa l'operació `setCrearMateriaPrimaSiNoExisteix`.
     *
     * @param crearMateriaPrimaSiNoExisteix paràmetre necessari per a
     * l'operació.
     */
    public void setCrearMateriaPrimaSiNoExisteix(boolean crearMateriaPrimaSiNoExisteix) {
        this.crearMateriaPrimaSiNoExisteix = crearMateriaPrimaSiNoExisteix;
    }
}
