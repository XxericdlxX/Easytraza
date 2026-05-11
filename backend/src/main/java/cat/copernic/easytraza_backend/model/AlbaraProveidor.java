package cat.copernic.easytraza_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public AlbaraProveidor() {
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

    public Proveidor getProveidor() {
        return proveidor;
    }

    public void setProveidor(Proveidor proveidor) {
        this.proveidor = proveidor;
    }

    public Usuari getUsuariReceptor() {
        return usuariReceptor;
    }

    public void setUsuariReceptor(Usuari usuariReceptor) {
        this.usuariReceptor = usuariReceptor;
    }

    public List<LotProveidor> getLots() {
        return lots;
    }

    public void setLots(List<LotProveidor> lots) {
        this.lots = lots;
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
}
