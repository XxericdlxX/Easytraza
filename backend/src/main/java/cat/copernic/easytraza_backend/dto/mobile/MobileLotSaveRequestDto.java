package cat.copernic.easytraza_backend.dto.mobile;

/**
 * DTO `MobileLotSaveRequestDto` del projecte EasyTraza.
 */
public class MobileLotSaveRequestDto {

    private String codiLot;
    private String codiMateriaPrimaOcr;
    private Double quantitat;
    private String materiaPrimaNom;
    private boolean crearMateriaPrimaSiNoExisteix;

    /**
     * Crea una nova instància del component.
     */
    public MobileLotSaveRequestDto() {
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
     * Executa l'operació `getMateriaPrimaNom`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getMateriaPrimaNom() {
        return materiaPrimaNom;
    }

    /**
     * Executa l'operació `setMateriaPrimaNom`.
     *
     * @param materiaPrimaNom paràmetre necessari per a l'operació.
     */
    public void setMateriaPrimaNom(String materiaPrimaNom) {
        this.materiaPrimaNom = materiaPrimaNom;
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
