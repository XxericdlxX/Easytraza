package cat.copernic.easytraza_backend.model;

import jakarta.persistence.*;

/**
 * Entitat del model `Producte` del projecte EasyTraza.
 */
@Entity
@Table(name = "productes")
public class Producte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(nullable = true, length = 255)
    private String descripcio;

    /**
     * Crea una nova instància del component.
     */
    public Producte() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param descripcio paràmetre necessari per a l'operació.
     */
    public Producte(Long id, String nom, String descripcio) {
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
