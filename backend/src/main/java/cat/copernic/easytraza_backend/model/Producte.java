package cat.copernic.easytraza_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productes")
public class Producte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String descripcio;

    public Producte() {
    }

    public Producte(Long id, String descripcio) {
        this.id = id;
        this.descripcio = descripcio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
}
