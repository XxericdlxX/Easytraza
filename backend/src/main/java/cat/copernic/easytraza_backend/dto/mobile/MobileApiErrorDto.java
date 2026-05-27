package cat.copernic.easytraza_backend.dto.mobile;

/**
 * DTO `MobileApiErrorDto` del projecte EasyTraza.
 */
public class MobileApiErrorDto {

    private String codi;
    private String missatge;

    /**
     * Crea una nova instància del component.
     */
    public MobileApiErrorDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param codi paràmetre necessari per a l'operació.
     * @param missatge paràmetre necessari per a l'operació.
     */
    public MobileApiErrorDto(String codi, String missatge) {
        this.codi = codi;
        this.missatge = missatge;
    }

    /**
     * Executa l'operació `getCodi`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getCodi() {
        return codi;
    }

    /**
     * Executa l'operació `setCodi`.
     *
     * @param codi paràmetre necessari per a l'operació.
     */
    public void setCodi(String codi) {
        this.codi = codi;
    }

    /**
     * Executa l'operació `getMissatge`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getMissatge() {
        return missatge;
    }

    /**
     * Executa l'operació `setMissatge`.
     *
     * @param missatge paràmetre necessari per a l'operació.
     */
    public void setMissatge(String missatge) {
        this.missatge = missatge;
    }
}
