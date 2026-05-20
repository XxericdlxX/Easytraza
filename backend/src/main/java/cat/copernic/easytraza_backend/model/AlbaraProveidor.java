package cat.copernic.easytraza_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitat del model `AlbaraProveidor` del projecte EasyTraza.
 */
@Entity
@Table(name = "albarans_proveidor")
public class AlbaraProveidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataRecepcio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proveidor_cif", nullable = false)
    private Proveidor proveidor;

    @ManyToOne
    @JoinColumn(name = "usuari_id")
    private Usuari usuariReceptor;

    @OneToMany(mappedBy = "albaraProveidor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LotProveidor> lots = new ArrayList<>();

    @Column(name = "document_ocr_nom_original")
    private String documentOcrNomOriginal;

    @Column(name = "document_ocr_nom_guardat")
    private String documentOcrNomGuardat;

    @Column(name = "document_ocr_content_type")
    private String documentOcrContentType;

    @Column(name = "document_ocr_ruta")
    private String documentOcrRuta;

    /**
     * Crea una nova instància del component.
     */
    public AlbaraProveidor() {
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
     * Executa l'operació `getProveidor`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Proveidor getProveidor() {
        return proveidor;
    }

    /**
     * Executa l'operació `setProveidor`.
     *
     * @param proveidor paràmetre necessari per a l'operació.
     */
    public void setProveidor(Proveidor proveidor) {
        this.proveidor = proveidor;
    }

    /**
     * Executa l'operació `getUsuariReceptor`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Usuari getUsuariReceptor() {
        return usuariReceptor;
    }

    /**
     * Executa l'operació `setUsuariReceptor`.
     *
     * @param usuariReceptor paràmetre necessari per a l'operació.
     */
    public void setUsuariReceptor(Usuari usuariReceptor) {
        this.usuariReceptor = usuariReceptor;
    }

    /**
     * Executa l'operació `getLots`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LotProveidor> getLots() {
        return lots;
    }

    /**
     * Executa l'operació `setLots`.
     *
     * @param lots paràmetre necessari per a l'operació.
     */
    public void setLots(List<LotProveidor> lots) {
        this.lots = lots;
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
}
