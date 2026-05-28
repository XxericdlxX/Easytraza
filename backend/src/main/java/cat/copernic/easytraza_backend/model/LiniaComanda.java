package cat.copernic.easytraza_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entitat que representa una línia de producte dins d'una comanda.
 */
@Entity
@Table(name = "linies_comanda")
public class LiniaComanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producte_id", nullable = false)
    private Producte producte;

    @Column(nullable = false)
    private Integer quantitat;

    @Column(length = 500)
    private String observacions;

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
    public Comanda getComanda() {
        return comanda;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param comanda paràmetre necessari per executar l'operació.
     */
    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Producte getProducte() {
        return producte;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param producte paràmetre necessari per executar l'operació.
     */
    public void setProducte(Producte producte) {
        this.producte = producte;
    }

    /**
     * Retorna el valor associat a aquesta propietat.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Integer getQuantitat() {
        return quantitat;
    }

    /**
     * Assigna el valor indicat a aquesta propietat.
     *
     * @param quantitat paràmetre necessari per executar l'operació.
     */
    public void setQuantitat(Integer quantitat) {
        this.quantitat = quantitat;
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
}
