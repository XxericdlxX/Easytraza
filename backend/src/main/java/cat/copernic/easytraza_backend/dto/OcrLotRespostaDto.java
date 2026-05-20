package cat.copernic.easytraza_backend.dto;

/**
 * DTO `OcrLotRespostaDto` del projecte EasyTraza.
 */
public class OcrLotRespostaDto {

    private String codiLot;
    private String codiMateriaPrimaOcr;
    private String materiaPrima;
    private Double quantitat;

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
     * Executa l'operació `getMateriaPrima`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getMateriaPrima() {
        return materiaPrima;
    }

    /**
     * Executa l'operació `setMateriaPrima`.
     *
     * @param materiaPrima paràmetre necessari per a l'operació.
     */
    public void setMateriaPrima(String materiaPrima) {
        this.materiaPrima = materiaPrima;
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
}
