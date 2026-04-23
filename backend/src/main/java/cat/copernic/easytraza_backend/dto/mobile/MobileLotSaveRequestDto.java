package cat.copernic.easytraza_backend.dto.mobile;

public class MobileLotSaveRequestDto {

    private String codiLot;
    private Integer quantitat;
    private String materiaPrimaNom;

    public String getCodiLot() {
        return codiLot;
    }

    public void setCodiLot(String codiLot) {
        this.codiLot = codiLot;
    }

    public Integer getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Integer quantitat) {
        this.quantitat = quantitat;
    }

    public String getMateriaPrimaNom() {
        return materiaPrimaNom;
    }

    public void setMateriaPrimaNom(String materiaPrimaNom) {
        this.materiaPrimaNom = materiaPrimaNom;
    }
}
