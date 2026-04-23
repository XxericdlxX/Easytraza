package cat.copernic.easytraza_backend.dto.mobile;

import java.util.ArrayList;
import java.util.List;

public class MobileAlbaraSaveRequestDto {

    private String dataRecepcio;
    private String proveidorCif;
    private String proveidorNom;
    private List<MobileLotSaveRequestDto> lots = new ArrayList<>();

    public String getDataRecepcio() {
        return dataRecepcio;
    }

    public void setDataRecepcio(String dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    public String getProveidorCif() {
        return proveidorCif;
    }

    public void setProveidorCif(String proveidorCif) {
        this.proveidorCif = proveidorCif;
    }

    public String getProveidorNom() {
        return proveidorNom;
    }

    public void setProveidorNom(String proveidorNom) {
        this.proveidorNom = proveidorNom;
    }

    public List<MobileLotSaveRequestDto> getLots() {
        return lots;
    }

    public void setLots(List<MobileLotSaveRequestDto> lots) {
        this.lots = lots;
    }
}
