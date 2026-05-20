package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.EstatAlbaraClient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitat del model `AlbaraClient` del projecte EasyTraza.
 */
@Entity
@Table(name = "albarans_client")
public class AlbaraClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataProduccio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstatAlbaraClient estat = EstatAlbaraClient.PENDENT_LLIURAR;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_nif", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "usuari_creador_id")
    private Usuari usuariCreador;

    @OneToMany(mappedBy = "albaraClient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiniaClient> linies = new ArrayList<>();

    /**
     * Crea una nova instància del component.
     */
    public AlbaraClient() {
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
     * Executa l'operació `getDataProduccio`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public LocalDate getDataProduccio() {
        return dataProduccio;
    }

    /**
     * Executa l'operació `setDataProduccio`.
     *
     * @param dataProduccio paràmetre necessari per a l'operació.
     */
    public void setDataProduccio(LocalDate dataProduccio) {
        this.dataProduccio = dataProduccio;
    }

    /**
     * Executa l'operació `getEstat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public EstatAlbaraClient getEstat() {
        return estat;
    }

    /**
     * Executa l'operació `setEstat`.
     *
     * @param estat paràmetre necessari per a l'operació.
     */
    public void setEstat(EstatAlbaraClient estat) {
        this.estat = estat;
    }

    /**
     * Executa l'operació `getClient`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Executa l'operació `setClient`.
     *
     * @param client paràmetre necessari per a l'operació.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Executa l'operació `getUsuariCreador`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Usuari getUsuariCreador() {
        return usuariCreador;
    }

    /**
     * Executa l'operació `setUsuariCreador`.
     *
     * @param usuariCreador paràmetre necessari per a l'operació.
     */
    public void setUsuariCreador(Usuari usuariCreador) {
        this.usuariCreador = usuariCreador;
    }

    /**
     * Executa l'operació `getLinies`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<LiniaClient> getLinies() {
        return linies;
    }

    /**
     * Executa l'operació `setLinies`.
     *
     * @param linies paràmetre necessari per a l'operació.
     */
    public void setLinies(List<LiniaClient> linies) {
        this.linies = linies;
    }
}
