package cat.copernic.easytraza_backend.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class AlbaraProveidorDto {

    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataRecepcio;

    private String proveidorCif;

    private String proveidorNomDetectat;

    private boolean crearProveidorSiNoExisteix;

    private String usuariReceptorNom;

    private String documentOcrNomOriginal;

    private String documentOcrNomGuardat;

    private String documentOcrContentType;

    private String documentOcrRuta;

    private List<LotProveidorDto> lots = new ArrayList<>();

    public AlbaraProveidorDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataRecepcio() {
        return dataRecepcio;
    }

    public void setDataRecepcio(LocalDate dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    public String getProveidorCif() {
        return proveidorCif;
    }

    public void setProveidorCif(String proveidorCif) {
        this.proveidorCif = proveidorCif;
    }

    public String getProveidorNomDetectat() {
        return proveidorNomDetectat;
    }

    public void setProveidorNomDetectat(String proveidorNomDetectat) {
        this.proveidorNomDetectat = proveidorNomDetectat;
    }

    public boolean isCrearProveidorSiNoExisteix() {
        return crearProveidorSiNoExisteix;
    }

    public void setCrearProveidorSiNoExisteix(boolean crearProveidorSiNoExisteix) {
        this.crearProveidorSiNoExisteix = crearProveidorSiNoExisteix;
    }

    public String getUsuariReceptorNom() {
        return usuariReceptorNom;
    }

    public void setUsuariReceptorNom(String usuariReceptorNom) {
        this.usuariReceptorNom = usuariReceptorNom;
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

    public List<LotProveidorDto> getLots() {
        return lots;
    }

    public void setLots(List<LotProveidorDto> lots) {
        this.lots = lots;
    }
}
