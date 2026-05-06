package cat.copernic.easytraza_backend.dto.mobile;

public class MobileLotSaveRequestDto {

    private String codiLot;
    private Double quantitat;
    private String materiaPrimaNom;
    private boolean crearMateriaPrimaSiNoExisteix;

    public MobileLotSaveRequestDto() {
    }

    public String getCodiLot() {
        return codiLot;
    }

    public void setCodiLot(String codiLot) {
        this.codiLot = codiLot;
    }

    public Double getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Double quantitat) {
        this.quantitat = quantitat;
    }

    public String getMateriaPrimaNom() {
        return materiaPrimaNom;
    }

    public void setMateriaPrimaNom(String materiaPrimaNom) {
        this.materiaPrimaNom = materiaPrimaNom;
    }

    public boolean isCrearMateriaPrimaSiNoExisteix() {
        return crearMateriaPrimaSiNoExisteix;
    }

    public void setCrearMateriaPrimaSiNoExisteix(boolean crearMateriaPrimaSiNoExisteix) {
        this.crearMateriaPrimaSiNoExisteix = crearMateriaPrimaSiNoExisteix;
    }
}
