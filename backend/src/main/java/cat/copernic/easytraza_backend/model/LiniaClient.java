package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.EstatLiniaClient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "linies_client")
public class LiniaClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numeroLotIntern;

    @Column(nullable = false)
    private Integer quantitat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstatLiniaClient estat = EstatLiniaClient.SENSE_LLIURAR;

    @ManyToOne(optional = false)
    @JoinColumn(name = "albara_client_id", nullable = false)
    private AlbaraClient albaraClient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producte_id", nullable = false)
    private Producte producte;

    @ManyToOne
    @JoinColumn(name = "operari_id")
    private Usuari operari;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "linies_client_lots_proveidor",
            joinColumns = @JoinColumn(name = "linia_client_id"),
            inverseJoinColumns = @JoinColumn(name = "lot_proveidor_id")
    )
    private Set<LotProveidor> lotsAssociats = new LinkedHashSet<>();

    public LiniaClient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroLotIntern() {
        return numeroLotIntern;
    }

    public void setNumeroLotIntern(Integer numeroLotIntern) {
        this.numeroLotIntern = numeroLotIntern;
    }

    public Integer getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Integer quantitat) {
        this.quantitat = quantitat;
    }

    public EstatLiniaClient getEstat() {
        return estat;
    }

    public void setEstat(EstatLiniaClient estat) {
        this.estat = estat;
    }

    public AlbaraClient getAlbaraClient() {
        return albaraClient;
    }

    public void setAlbaraClient(AlbaraClient albaraClient) {
        this.albaraClient = albaraClient;
    }

    public Producte getProducte() {
        return producte;
    }

    public void setProducte(Producte producte) {
        this.producte = producte;
    }

    public Usuari getOperari() {
        return operari;
    }

    public void setOperari(Usuari operari) {
        this.operari = operari;
    }

    public Set<LotProveidor> getLotsAssociats() {
        return lotsAssociats;
    }

    public void setLotsAssociats(Set<LotProveidor> lotsAssociats) {
        this.lotsAssociats = lotsAssociats;
    }
}
