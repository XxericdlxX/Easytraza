package cat.copernic.easytraza_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class OcrAlbaraResponseDto {

    private String proveidorCif;
    private String numeroAlbara;
    private String dataAlbara;
    private String textDetectat;
    private String documentOcrNomOriginal;
    private String documentOcrNomGuardat;
    private String documentOcrContentType;
    private String documentOcrRuta;
    private List<OcrLotRespostaDto> lots = new ArrayList<>();

    public String getProveidorCif() {
        return proveidorCif;
    }

    public void setProveidorCif(String proveidorCif) {
        this.proveidorCif = proveidorCif;
    }

    public String getNumeroAlbara() {
        return numeroAlbara;
    }

    public void setNumeroAlbara(String numeroAlbara) {
        this.numeroAlbara = numeroAlbara;
    }

    public String getDataAlbara() {
        return dataAlbara;
    }

    public void setDataAlbara(String dataAlbara) {
        this.dataAlbara = dataAlbara;
    }

    public String getTextDetectat() {
        return textDetectat;
    }

    public void setTextDetectat(String textDetectat) {
        this.textDetectat = textDetectat;
    }

    public String getDocumentOcrNomOriginal() {
        return documentOcrNomOriginal;
    }

    public void setDocumentOcrNomOriginal(String documentOcrNomOriginal) {
        this.documentOcrNomOriginal = documentOcrNomOriginal;
    }

    public String getDocumentOcrNomGuardat() {
        return documentOcrNomGuardat;
    }

    public void setDocumentOcrNomGuardat(String documentOcrNomGuardat) {
        this.documentOcrNomGuardat = documentOcrNomGuardat;
    }

    public String getDocumentOcrContentType() {
        return documentOcrContentType;
    }

    public void setDocumentOcrContentType(String documentOcrContentType) {
        this.documentOcrContentType = documentOcrContentType;
    }

    public String getDocumentOcrRuta() {
        return documentOcrRuta;
    }

    public void setDocumentOcrRuta(String documentOcrRuta) {
        this.documentOcrRuta = documentOcrRuta;
    }

    public List<OcrLotRespostaDto> getLots() {
        return lots;
    }

    public void setLots(List<OcrLotRespostaDto> lots) {
        this.lots = lots;
    }
}
