package cat.copernic.easytraza_backend.model;

import jakarta.persistence.*;

/**
 * Entitat del model `MateriaPrima` del projecte EasyTraza.
 */
@Entity
@Table(name = "materies_primeres")
public class MateriaPrima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String descripcio;

    /**
     * Crea una nova instància del component.
     */
    public MateriaPrima() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param descripcio paràmetre necessari per a l'operació.
     */
    public MateriaPrima(Long id, String nom, String descripcio) {
        this.id = id;
        this.nom = nom;
        this.descripcio = descripcio;
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
     * Executa l'operació `getNom`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Executa l'operació `setNom`.
     *
     * @param nom paràmetre necessari per a l'operació.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Executa l'operació `getDescripcio`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getDescripcio() {
        return descripcio;
    }

    /**
     * Executa l'operació `setDescripcio`.
     *
     * @param descripcio paràmetre necessari per a l'operació.
     */
    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
}
