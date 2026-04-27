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

    public AlbaraClient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataProduccio() {
        return dataProduccio;
    }

    public void setDataProduccio(LocalDate dataProduccio) {
        this.dataProduccio = dataProduccio;
    }

    public EstatAlbaraClient getEstat() {
        return estat;
    }

    public void setEstat(EstatAlbaraClient estat) {
        this.estat = estat;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Usuari getUsuariCreador() {
        return usuariCreador;
    }

    public void setUsuariCreador(Usuari usuariCreador) {
        this.usuariCreador = usuariCreador;
    }

    public List<LiniaClient> getLinies() {
        return linies;
    }

    public void setLinies(List<LiniaClient> linies) {
        this.linies = linies;
    }
}
