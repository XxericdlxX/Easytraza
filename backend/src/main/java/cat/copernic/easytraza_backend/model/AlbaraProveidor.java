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

    @OneToMany(mappedBy = "albaraProveidor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LotProveidor> lots = new ArrayList<>();

    public AlbaraProveidor() {
    }

    public AlbaraProveidor(Long id, LocalDate dataRecepcio, Proveidor proveidor) {
        this.id = id;
        this.dataRecepcio = dataRecepcio;
        this.proveidor = proveidor;
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

    public List<LotProveidor> getLots() {
        return lots;
    }

    public void setLots(List<LotProveidor> lots) {
        this.lots = lots;
    }

    public void afegirLot(LotProveidor lot) {
        lots.add(lot);
        lot.setAlbaraProveidor(this);
    }

    public void eliminarLot(LotProveidor lot) {
        lots.remove(lot);
        lot.setAlbaraProveidor(null);
    }
}
