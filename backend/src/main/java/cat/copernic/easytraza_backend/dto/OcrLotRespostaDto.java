package cat.copernic.easytraza_backend.dto;

public class OcrLotRespostaDto {

    private String codiLot;
    private String codiMateriaPrimaOcr;
    private String materiaPrima;
    private Double quantitat;

    public String getCodiLot() {
        return codiLot;
    }

    public void setCodiLot(String codiLot) {
        this.codiLot = codiLot;
    }

    public String getCodiMateriaPrimaOcr() {
        return codiMateriaPrimaOcr;
    }

    public void setCodiMateriaPrimaOcr(String codiMateriaPrimaOcr) {
        this.codiMateriaPrimaOcr = codiMateriaPrimaOcr;
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
