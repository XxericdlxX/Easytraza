package cat.copernic.easytraza_backend.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO `AlbaraProveidorDto` del projecte EasyTraza.
 */
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

    /**
     * Crea una nova instància del component.
     */
    public AlbaraProveidorDto() {
    }

    /**
     * Executa l'operació `getId`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Long getId() {
        return id;
    }

    /**
     * Executa l'operació `setId`.
     *
     * @param id paràmetre necessari per a l'operació.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Executa l'operació `getDataRecepcio`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public LocalDate getDataRecepcio() {
        return dataRecepcio;
    }

    /**
     * Executa l'operació `setDataRecepcio`.
     *
     * @param dataRecepcio paràmetre necessari per a l'operació.
     */
    public void setDataRecepcio(LocalDate dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

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
     * Executa l'operació `getProveidorNomDetectat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getProveidorNomDetectat() {
        return proveidorNomDetectat;
    }

    /**
     * Executa l'operació `setProveidorNomDetectat`.
     *
     * @param proveidorNomDetectat paràmetre necessari per a l'operació.
     */
    public void setProveidorNomDetectat(String proveidorNomDetectat) {
        this.proveidorNomDetectat = proveidorNomDetectat;
    }

    /**
     * Executa l'operació `isCrearProveidorSiNoExisteix`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public boolean isCrearProveidorSiNoExisteix() {
        return crearProveidorSiNoExisteix;
    }

    /**
     * Executa l'operació `setCrearProveidorSiNoExisteix`.
     *
     * @param crearProveidorSiNoExisteix paràmetre necessari per a l'operació.
     */
    public void setCrearProveidorSiNoExisteix(boolean crearProveidorSiNoExisteix) {
        this.crearProveidorSiNoExisteix = crearProveidorSiNoExisteix;
    }

    /**
     * Executa l'operació `getUsuariReceptorNom`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getUsuariReceptorNom() {
        return usuariReceptorNom;
    }

    /**
     * Executa l'operació `setUsuariReceptorNom`.
     *
     * @param usuariReceptorNom paràmetre necessari per a l'operació.
     */
    public void setUsuariReceptorNom(String usuariReceptorNom) {
        this.usuariReceptorNom = usuariReceptorNom;
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
    public List<LotProveidorDto> getLots() {
        return lots;
    }

    /**
     * Executa l'operació `setLots`.
     *
     * @param lots paràmetre necessari per a l'operació.
     */
    public void setLots(List<LotProveidorDto> lots) {
        this.lots = lots;
    }
}
