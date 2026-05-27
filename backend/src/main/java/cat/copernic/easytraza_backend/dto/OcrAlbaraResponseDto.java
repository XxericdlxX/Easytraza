package cat.copernic.easytraza_backend.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO `OcrAlbaraResponseDto` del projecte EasyTraza.
 */
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

    /**
     * Executa l'operació `getProveidorCif`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getProveidorCif() {
        return proveidorCif;
    }

    /**
     * Executa l'operació `setProveidorCif`.
     *
     * @param proveidorCif paràmetre necessari per a l'operació.
     */
    public void setProveidorCif(String proveidorCif) {
        this.proveidorCif = proveidorCif;
    }

    /**
     * Executa l'operació `getNumeroAlbara`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getNumeroAlbara() {
        return numeroAlbara;
    }

    /**
     * Executa l'operació `setNumeroAlbara`.
     *
     * @param numeroAlbara paràmetre necessari per a l'operació.
     */
    public void setNumeroAlbara(String numeroAlbara) {
        this.numeroAlbara = numeroAlbara;
    }

    /**
     * Executa l'operació `getDataAlbara`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDataAlbara() {
        return dataAlbara;
    }

    /**
     * Executa l'operació `setDataAlbara`.
     *
     * @param dataAlbara paràmetre necessari per a l'operació.
     */
    public void setDataAlbara(String dataAlbara) {
        this.dataAlbara = dataAlbara;
    }

    /**
     * Executa l'operació `getTextDetectat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getTextDetectat() {
        return textDetectat;
    }

    /**
     * Executa l'operació `setTextDetectat`.
     *
     * @param textDetectat paràmetre necessari per a l'operació.
     */
    public void setTextDetectat(String textDetectat) {
        this.textDetectat = textDetectat;
    }

    /**
     * Executa l'operació `getDocumentOcrNomOriginal`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDocumentOcrNomOriginal() {
        return documentOcrNomOriginal;
    }

    /**
     * Executa l'operació `setDocumentOcrNomOriginal`.
     *
     * @param documentOcrNomOriginal paràmetre necessari per a l'operació.
     */
    public void setDocumentOcrNomOriginal(String documentOcrNomOriginal) {
        this.documentOcrNomOriginal = documentOcrNomOriginal;
    }

    /**
     * Executa l'operació `getDocumentOcrNomGuardat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDocumentOcrNomGuardat() {
        return documentOcrNomGuardat;
    }

    /**
     * Executa l'operació `setDocumentOcrNomGuardat`.
     *
     * @param documentOcrNomGuardat paràmetre necessari per a l'operació.
     */
    public void setDocumentOcrNomGuardat(String documentOcrNomGuardat) {
        this.documentOcrNomGuardat = documentOcrNomGuardat;
    }

    /**
     * Executa l'operació `getDocumentOcrContentType`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDocumentOcrContentType() {
        return documentOcrContentType;
    }

    /**
     * Executa l'operació `setDocumentOcrContentType`.
     *
     * @param documentOcrContentType paràmetre necessari per a l'operació.
     */
    public void setDocumentOcrContentType(String documentOcrContentType) {
        this.documentOcrContentType = documentOcrContentType;
    }

    /**
     * Executa l'operació `getDocumentOcrRuta`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDocumentOcrRuta() {
        return documentOcrRuta;
    }

    /**
     * Executa l'operació `setDocumentOcrRuta`.
     *
     * @param documentOcrRuta paràmetre necessari per a l'operació.
     */
    public void setDocumentOcrRuta(String documentOcrRuta) {
        this.documentOcrRuta = documentOcrRuta;
    }

    /**
     * Executa l'operació `getLots`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<OcrLotRespostaDto> getLots() {
        return lots;
    }

    /**
     * Executa l'operació `setLots`.
     *
     * @param lots paràmetre necessari per a l'operació.
     */
    public void setLots(List<OcrLotRespostaDto> lots) {
        this.lots = lots;
    }
}
