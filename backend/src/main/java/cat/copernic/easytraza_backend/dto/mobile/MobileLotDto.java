package cat.copernic.easytraza_backend.dto.mobile;

/**
 * DTO `MobileLotDto` del projecte EasyTraza.
 */
public class MobileLotDto {

    private Long id;
    private String codiLot;
    private Double quantitat;
    private String estat;
    private String materiaPrimaNom;
    private String proveidorNom;
    private String proveidorCif;
    private String dataRecepcio;
    private String dataObertura;
    private String dataAcabament;
    private Long albaraProveidorId;

    /**
     * Crea una nova instància del component.
     */
    public MobileLotDto() {
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
     * Executa l'operació `getEstat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getEstat() {
        return estat;
    }

    /**
     * Executa l'operació `setEstat`.
     *
     * @param estat paràmetre necessari per a l'operació.
     */
    public void setEstat(String estat) {
        this.estat = estat;
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
     * Executa l'operació `getProveidorNom`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getProveidorNom() {
        return proveidorNom;
    }

    /**
     * Executa l'operació `setProveidorNom`.
     *
     * @param proveidorNom paràmetre necessari per a l'operació.
     */
    public void setProveidorNom(String proveidorNom) {
        this.proveidorNom = proveidorNom;
    }

    /**
     * Executa l'operació `getProveidorCif`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getProveidorCif() {
        return proveidorCif;
    }

    /**
     * Executa l'operació `setProveidorCif`.
     *
     * @param proveidorCif paràmetre necessari per a l'operació.
     */
    public void setProveidorCif(String proveidorCif) {
        this.proveidorCif = proveidorCif;
    }

    /**
     * Executa l'operació `getDataRecepcio`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDataRecepcio() {
        return dataRecepcio;
    }

    /**
     * Executa l'operació `setDataRecepcio`.
     *
     * @param dataRecepcio paràmetre necessari per a l'operació.
     */
    public void setDataRecepcio(String dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    /**
     * Executa l'operació `getDataObertura`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDataObertura() {
        return dataObertura;
    }

    /**
     * Executa l'operació `setDataObertura`.
     *
     * @param dataObertura paràmetre necessari per a l'operació.
     */
    public void setDataObertura(String dataObertura) {
        this.dataObertura = dataObertura;
    }

    /**
     * Executa l'operació `getDataAcabament`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDataAcabament() {
        return dataAcabament;
    }

    /**
     * Executa l'operació `setDataAcabament`.
     *
     * @param dataAcabament paràmetre necessari per a l'operació.
     */
    public void setDataAcabament(String dataAcabament) {
        this.dataAcabament = dataAcabament;
    }

    /**
     * Executa l'operació `getAlbaraProveidorId`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Long getAlbaraProveidorId() {
        return albaraProveidorId;
    }

    /**
     * Executa l'operació `setAlbaraProveidorId`.
     *
     * @param albaraProveidorId paràmetre necessari per a l'operació.
     */
    public void setAlbaraProveidorId(Long albaraProveidorId) {
        this.albaraProveidorId = albaraProveidorId;
    }
}
