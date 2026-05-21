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

/**
 * Entitat del model `LiniaClient` del projecte EasyTraza.
 */
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

    /**
     * Crea una nova instància del component.
     */
    public LiniaClient() {
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
     * Executa l'operació `getNumeroLotIntern`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Integer getNumeroLotIntern() {
        return numeroLotIntern;
    }

    /**
     * Executa l'operació `setNumeroLotIntern`.
     *
     * @param numeroLotIntern paràmetre necessari per a l'operació.
     */
    public void setNumeroLotIntern(Integer numeroLotIntern) {
        this.numeroLotIntern = numeroLotIntern;
    }

    /**
     * Executa l'operació `getQuantitat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Integer getQuantitat() {
        return quantitat;
    }

    /**
     * Executa l'operació `setQuantitat`.
     *
     * @param quantitat paràmetre necessari per a l'operació.
     */
    public void setQuantitat(Integer quantitat) {
        this.quantitat = quantitat;
    }

    /**
     * Executa l'operació `getEstat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public EstatLiniaClient getEstat() {
        return estat;
    }

    /**
     * Executa l'operació `setEstat`.
     *
     * @param estat paràmetre necessari per a l'operació.
     */
    public void setEstat(EstatLiniaClient estat) {
        this.estat = estat;
    }

    /**
     * Executa l'operació `getAlbaraClient`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public AlbaraClient getAlbaraClient() {
        return albaraClient;
    }

    /**
     * Executa l'operació `setAlbaraClient`.
     *
     * @param albaraClient paràmetre necessari per a l'operació.
     */
    public void setAlbaraClient(AlbaraClient albaraClient) {
        this.albaraClient = albaraClient;
    }

    /**
     * Executa l'operació `getProducte`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Producte getProducte() {
        return producte;
    }

    /**
     * Executa l'operació `setProducte`.
     *
     * @param producte paràmetre necessari per a l'operació.
     */
    public void setProducte(Producte producte) {
        this.producte = producte;
    }

    /**
     * Executa l'operació `getOperari`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Usuari getOperari() {
        return operari;
    }

    /**
     * Executa l'operació `setOperari`.
     *
     * @param operari paràmetre necessari per a l'operació.
     */
    public void setOperari(Usuari operari) {
        this.operari = operari;
    }

    /**
     * Executa l'operació `getLotsAssociats`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Set<LotProveidor> getLotsAssociats() {
        return lotsAssociats;
    }

    /**
     * Executa l'operació `setLotsAssociats`.
     *
     * @param lotsAssociats paràmetre necessari per a l'operació.
     */
    public void setLotsAssociats(Set<LotProveidor> lotsAssociats) {
        this.lotsAssociats = lotsAssociats;
    }
}
