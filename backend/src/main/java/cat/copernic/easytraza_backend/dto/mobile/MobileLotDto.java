package cat.copernic.easytraza_backend.dto.mobile;

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

    public MobileLotDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }

    public String getMateriaPrimaNom() {
        return materiaPrimaNom;
    }

    public void setMateriaPrimaNom(String materiaPrimaNom) {
        this.materiaPrimaNom = materiaPrimaNom;
    }

    public String getProveidorNom() {
        return proveidorNom;
    }

    public void setProveidorNom(String proveidorNom) {
        this.proveidorNom = proveidorNom;
    }

    public String getProveidorCif() {
        return proveidorCif;
    }

    public void setProveidorCif(String proveidorCif) {
        this.proveidorCif = proveidorCif;
    }

    public String getDataRecepcio() {
        return dataRecepcio;
    }

    public void setDataRecepcio(String dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    public String getDataObertura() {
        return dataObertura;
    }

    public void setDataObertura(String dataObertura) {
        this.dataObertura = dataObertura;
    }

    public String getDataAcabament() {
        return dataAcabament;
    }

    public void setDataAcabament(String dataAcabament) {
        this.dataAcabament = dataAcabament;
    }

    public Long getAlbaraProveidorId() {
        return albaraProveidorId;
    }

    public void setAlbaraProveidorId(Long albaraProveidorId) {
        this.albaraProveidorId = albaraProveidorId;
    }
}
