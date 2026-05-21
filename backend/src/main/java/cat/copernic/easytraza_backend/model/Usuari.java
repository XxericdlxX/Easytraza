package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.Rol;
import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Entitat del model `Usuari` del projecte EasyTraza.
 */
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

    @Column(length = 20)
    private String nif;

    @Column(name = "foto_perfil_nom", length = 255)
    private String fotoPerfilNom;

    @Column(nullable = true)
    private String contrasenya;

    @Column(name = "token_recuperacio_contrasenya", length = 120)
    private String tokenRecuperacioContrasenya;

    @Column(name = "token_recuperacio_expiracio")
    private LocalDateTime tokenRecuperacioExpiracio;

    /**
     * Crea una nova instància del component.
     */
    public Usuari() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param cognoms paràmetre necessari per a l'operació.
     * @param rol paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @param contrasenya paràmetre necessari per a l'operació.
     */
    public Usuari(Long id, String nom, String cognoms, Rol rol, String email, String contrasenya) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.rol = rol;
        this.email = email;
        this.contrasenya = contrasenya;
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
     * Executa l'operació `getCognoms`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getCognoms() {
        return cognoms;
    }

    /**
     * Executa l'operació `setCognoms`.
     *
     * @param cognoms paràmetre necessari per a l'operació.
     */
    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    /**
     * Executa l'operació `getRol`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Rol getRol() {
        return rol;
    }

    /**
     * Executa l'operació `setRol`.
     *
     * @param rol paràmetre necessari per a l'operació.
     */
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /**
     * Executa l'operació `getEmail`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Executa l'operació `setEmail`.
     *
     * @param email paràmetre necessari per a l'operació.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Executa l'operació `getNif`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getNif() {
        return nif;
    }

    /**
     * Executa l'operació `setNif`.
     *
     * @param nif paràmetre necessari per a l'operació.
     */
    public void setNif(String nif) {
        this.nif = nif;
    }

    /**
     * Executa l'operació `getFotoPerfilNom`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getFotoPerfilNom() {
        return fotoPerfilNom;
    }

    /**
     * Executa l'operació `setFotoPerfilNom`.
     *
     * @param fotoPerfilNom paràmetre necessari per a l'operació.
     */
    public void setFotoPerfilNom(String fotoPerfilNom) {
        this.fotoPerfilNom = fotoPerfilNom;
    }

    /**
     * Executa l'operació `getContrasenya`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getContrasenya() {
        return contrasenya;
    }

    /**
     * Executa l'operació `setContrasenya`.
     *
     * @param contrasenya paràmetre necessari per a l'operació.
     */
    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    /**
     * Executa l'operació `getTokenRecuperacioContrasenya`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getTokenRecuperacioContrasenya() {
        return tokenRecuperacioContrasenya;
    }

    /**
     * Executa l'operació `setTokenRecuperacioContrasenya`.
     *
     * @param tokenRecuperacioContrasenya paràmetre necessari per a l'operació.
     */
    public void setTokenRecuperacioContrasenya(String tokenRecuperacioContrasenya) {
        this.tokenRecuperacioContrasenya = tokenRecuperacioContrasenya;
    }

    /**
     * Executa l'operació `getTokenRecuperacioExpiracio`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public LocalDateTime getTokenRecuperacioExpiracio() {
        return tokenRecuperacioExpiracio;
    }

    /**
     * Executa l'operació `setTokenRecuperacioExpiracio`.
     *
     * @param tokenRecuperacioExpiracio paràmetre necessari per a l'operació.
     */
    public void setTokenRecuperacioExpiracio(LocalDateTime tokenRecuperacioExpiracio) {
        this.tokenRecuperacioExpiracio = tokenRecuperacioExpiracio;
    }
}
