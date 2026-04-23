package cat.copernic.easytraza_backend.dto;

public class OcrLotRespostaDto {

    private String codiLot;
    private String materiaPrima;
    private Double quantitat;

    public String getCodiLot() {
        return codiLot;
    }

    public void setCodiLot(String codiLot) {
        this.codiLot = codiLot;
    }

    public String getMateriaPrima() {
        return materiaPrima;
    }

    public void setMateriaPrima(String materiaPrima) {
        this.materiaPrima = materiaPrima;
    }

    public Double getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Double quantitat) {
        this.quantitat = quantitat;
    }
}
