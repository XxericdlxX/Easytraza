package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.EstatComanda;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitat que representa una comanda d'un client.
 *
 * La comanda és el pedido previ del client. Quan cal registrar la sortida real
 * del producte, es pot generar un albarà de client associat.
 */
@Entity
@Table(name = "comandes")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataComanda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstatComanda estat = EstatComanda.PENDENT;

    @Column(length = 500)
    private String observacions;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_nif", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "usuari_creador_id")
    private Usuari usuariCreador;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "albara_client_id", unique = true)
    private AlbaraClient albaraClient;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiniaComanda> linies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param id paràmetre necessari per executar l'operació.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public LocalDate getDataComanda() {
        return dataComanda;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param dataComanda paràmetre necessari per executar l'operació.
     */
    public void setDataComanda(LocalDate dataComanda) {
        this.dataComanda = dataComanda;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public EstatComanda getEstat() {
        return estat;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param estat paràmetre necessari per executar l'operació.
     */
    public void setEstat(EstatComanda estat) {
        this.estat = estat;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getObservacions() {
        return observacions;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param observacions paràmetre necessari per executar l'operació.
     */
    public void setObservacions(String observacions) {
        this.observacions = observacions;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param client paràmetre necessari per executar l'operació.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Usuari getUsuariCreador() {
        return usuariCreador;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param usuariCreador paràmetre necessari per executar l'operació.
     */
    public void setUsuariCreador(Usuari usuariCreador) {
        this.usuariCreador = usuariCreador;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public AlbaraClient getAlbaraClient() {
        return albaraClient;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param albaraClient paràmetre necessari per executar l'operació.
     */
    public void setAlbaraClient(AlbaraClient albaraClient) {
        this.albaraClient = albaraClient;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LiniaComanda> getLinies() {
        return linies;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param linies paràmetre necessari per executar l'operació.
     */
    public void setLinies(List<LiniaComanda> linies) {
        this.linies = linies;
    }
}
