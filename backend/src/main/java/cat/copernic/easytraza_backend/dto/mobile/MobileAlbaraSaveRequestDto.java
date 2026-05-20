package cat.copernic.easytraza_backend.dto.mobile;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO `MobileAlbaraSaveRequestDto` del projecte EasyTraza.
 */
public class MobileAlbaraSaveRequestDto {

    private String dataRecepcio;
    private String proveidorCif;
    private String proveidorNom;
    private boolean crearProveidorSiNoExisteix;
    private Long usuariReceptorId;
    private List<MobileLotSaveRequestDto> lots = new ArrayList<>();

    /**
     * Crea una nova instància del component.
     */
    public MobileAlbaraSaveRequestDto() {
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
     * Executa l'operació `isCrearProveidorSiNoExisteix`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean isCrearProveidorSiNoExisteix() {
        return crearProveidorSiNoExisteix;
    }

    /**
     * Executa l'operació `setCrearProveidorSiNoExisteix`.
     *
     * @param crearProveidorSiNoExisteix paràmetre necessari per a l'operació.
     */
    public void setCrearProveidorSiNoExisteix(boolean crearProveidorSiNoExisteix) {
        this.crearProveidorSiNoExisteix = crearProveidorSiNoExisteix;
    }

    /**
     * Executa l'operació `getUsuariReceptorId`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Long getUsuariReceptorId() {
        return usuariReceptorId;
    }

    /**
     * Executa l'operació `setUsuariReceptorId`.
     *
     * @param usuariReceptorId paràmetre necessari per a l'operació.
     */
    public void setUsuariReceptorId(Long usuariReceptorId) {
        this.usuariReceptorId = usuariReceptorId;
    }

    /**
     * Executa l'operació `getLots`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<MobileLotSaveRequestDto> getLots() {
        return lots;
    }

    /**
     * Executa l'operació `setLots`.
     *
     * @param lots paràmetre necessari per a l'operació.
     */
    public void setLots(List<MobileLotSaveRequestDto> lots) {
        this.lots = lots;
    }
}
