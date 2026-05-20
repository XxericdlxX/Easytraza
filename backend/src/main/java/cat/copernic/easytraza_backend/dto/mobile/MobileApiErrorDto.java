package cat.copernic.easytraza_backend.dto.mobile;

public class MobileApiErrorDto {

    private String codi;
    private String missatge;

    public MobileApiErrorDto() {
    }

    public MobileApiErrorDto(String codi, String missatge) {
        this.codi = codi;
        this.missatge = missatge;
    }

    public String getCodi() {
        return codi;
    }

    public void setCodi(String codi) {
        this.codi = codi;
    }

    public String getMissatge() {
        return missatge;
    }

    public void setMissatge(String missatge) {
        this.missatge = missatge;
    }
}
