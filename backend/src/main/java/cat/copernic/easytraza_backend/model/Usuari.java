package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.Rol;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "usuaris")
public class Usuari {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String cognoms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.OPERARI;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String contrasenya;

    @Column(name = "token_recuperacio_contrasenya", length = 120)
    private String tokenRecuperacioContrasenya;

    @Column(name = "token_recuperacio_expiracio")
    private LocalDateTime tokenRecuperacioExpiracio;

    public Usuari() {
    }

    public Usuari(Long id, String nom, String cognoms, Rol rol, String email, String contrasenya) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.rol = rol;
        this.email = email;
        this.contrasenya = contrasenya;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognoms() {
        return cognoms;
    }

    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getTokenRecuperacioContrasenya() {
        return tokenRecuperacioContrasenya;
    }

    public void setTokenRecuperacioContrasenya(String tokenRecuperacioContrasenya) {
        this.tokenRecuperacioContrasenya = tokenRecuperacioContrasenya;
    }

    public LocalDateTime getTokenRecuperacioExpiracio() {
        return tokenRecuperacioExpiracio;
    }

    public void setTokenRecuperacioExpiracio(LocalDateTime tokenRecuperacioExpiracio) {
        this.tokenRecuperacioExpiracio = tokenRecuperacioExpiracio;
    }
}
