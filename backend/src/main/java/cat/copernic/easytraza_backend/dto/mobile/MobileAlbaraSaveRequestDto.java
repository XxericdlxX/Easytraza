package cat.copernic.easytraza_backend.dto.mobile;

import java.util.ArrayList;
import java.util.List;

public class MobileAlbaraSaveRequestDto {

    private String dataRecepcio;
    private String proveidorCif;
    private String proveidorNom;
    private boolean crearProveidorSiNoExisteix;
    private List<MobileLotSaveRequestDto> lots = new ArrayList<>();

    public MobileAlbaraSaveRequestDto() {
    }

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

    public boolean isCrearProveidorSiNoExisteix() {
        return crearProveidorSiNoExisteix;
    }

    public void setCrearProveidorSiNoExisteix(boolean crearProveidorSiNoExisteix) {
        this.crearProveidorSiNoExisteix = crearProveidorSiNoExisteix;
    }

    public List<MobileLotSaveRequestDto> getLots() {
        return lots;
    }

    public void setLots(List<MobileLotSaveRequestDto> lots) {
        this.lots = lots;
    }
}
