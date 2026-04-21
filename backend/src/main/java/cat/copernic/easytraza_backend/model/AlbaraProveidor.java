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
}
