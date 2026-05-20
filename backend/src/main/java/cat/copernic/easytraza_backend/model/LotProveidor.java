package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.EstatLot;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "lots_proveidor")
public class LotProveidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codi_lot", nullable = false, length = 100)
    private String codiLot;

    @Column(name = "codi_materia_prima_ocr", length = 120)
    private String codiMateriaPrimaOcr;

    @Column(nullable = false)
    private Double quantitat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstatLot estat;

    private LocalDate dataObertura;

    private LocalDate dataAcabament;

    @ManyToOne(optional = false)
    @JoinColumn(name = "materia_prima_id", nullable = false)
    private MateriaPrima materiaPrima;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proveidor_cif", nullable = false)
    private Proveidor proveidor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "albara_proveidor_id", nullable = false)
    private AlbaraProveidor albaraProveidor;

    public LotProveidor() {
    }

    public LotProveidor(Long id, String codiLot, Double quantitat, EstatLot estat,
            LocalDate dataObertura, LocalDate dataAcabament,
            MateriaPrima materiaPrima, Proveidor proveidor,
            AlbaraProveidor albaraProveidor) {
        this.id = id;
        this.codiLot = codiLot;
        this.quantitat = quantitat;
        this.estat = estat;
        this.dataObertura = dataObertura;
        this.dataAcabament = dataAcabament;
        this.materiaPrima = materiaPrima;
        this.proveidor = proveidor;
        this.albaraProveidor = albaraProveidor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodiLot() {
        return codiLot;
    }

    public void setCodiLot(String codiLot) {
        this.codiLot = codiLot;
    }

    public String getCodiMateriaPrimaOcr() {
        return codiMateriaPrimaOcr;
    }

    public void setCodiMateriaPrimaOcr(String codiMateriaPrimaOcr) {
        this.codiMateriaPrimaOcr = codiMateriaPrimaOcr;
    }

    public Double getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Double quantitat) {
        this.quantitat = quantitat;
    }

    public EstatLot getEstat() {
        return estat;
    }

    public void setEstat(EstatLot estat) {
        this.estat = estat;
    }

    public LocalDate getDataObertura() {
        return dataObertura;
    }

    public void setDataObertura(LocalDate dataObertura) {
        this.dataObertura = dataObertura;
    }

    public LocalDate getDataAcabament() {
        return dataAcabament;
    }

    public void setDataAcabament(LocalDate dataAcabament) {
        this.dataAcabament = dataAcabament;
    }

    public MateriaPrima getMateriaPrima() {
        return materiaPrima;
    }

    public void setMateriaPrima(MateriaPrima materiaPrima) {
        this.materiaPrima = materiaPrima;
    }

    public Proveidor getProveidor() {
        return proveidor;
    }

    public void setProveidor(Proveidor proveidor) {
        this.proveidor = proveidor;
    }

    public AlbaraProveidor getAlbaraProveidor() {
        return albaraProveidor;
    }

    public void setAlbaraProveidor(AlbaraProveidor albaraProveidor) {
        this.albaraProveidor = albaraProveidor;
    }
}
