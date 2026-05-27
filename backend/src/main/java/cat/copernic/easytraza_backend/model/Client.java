package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.TipusClient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entitat del model `Client` del projecte EasyTraza.
 */
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @NotBlank(message = "{clients.nif.obligatori}")
    @Size(max = 20, message = "{clients.nif.max}")
    @Column(nullable = false, unique = true, length = 20)
    private String nif;

    @NotBlank(message = "{clients.nom.obligatori}")
    @Size(max = 100, message = "{clients.nom.max}")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotNull(message = "{clients.tipus.obligatori}")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipusClient tipusClient;

    @Size(max = 100, message = "{clients.tipus.altres.max}")
    @Column(length = 100)
    private String tipusClientAltres;

    @NotBlank(message = "{clients.adreca.obligatoria}")
    @Size(max = 255, message = "{clients.adreca.max}")
    @Column(nullable = false, length = 255)
    private String adreca;

    @Size(max = 20, message = "{clients.telefon.max}")
    @Pattern(regexp = "^$|^[+0-9 ()-]{7,20}$", message = "{clients.telefon.invalid}")
    @Column(length = 20)
    private String telefon;

    @Size(max = 100, message = "{clients.email.max}")
    @Email(message = "{clients.email.invalid}")
    @Column(length = 100)
    private String email;

    @Size(max = 255, message = "{clients.notes.max}")
    @Column(length = 255)
    private String notes;

    /**
     * Crea una nova instància del component.
     */
    public Client() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param nif paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param tipusClient paràmetre necessari per a l'operació.
     * @param adreca paràmetre necessari per a l'operació.
     * @param telefon paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     * @param notes paràmetre necessari per a l'operació.
     */
    public Client(String nif, String nom, TipusClient tipusClient,
            String adreca, String telefon, String email, String notes) {
        this.nif = nif;
        this.nom = nom;
        this.tipusClient = tipusClient;
        this.adreca = adreca;
        this.telefon = telefon;
        this.email = email;
        this.notes = notes;
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
     * Executa l'operació `getTipusClient`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public TipusClient getTipusClient() {
        return tipusClient;
    }

    /**
     * Executa l'operació `setTipusClient`.
     *
     * @param tipusClient paràmetre necessari per a l'operació.
     */
    public void setTipusClient(TipusClient tipusClient) {
        this.tipusClient = tipusClient;
    }

    /**
     * Executa l'operació `getTipusClientAltres`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getTipusClientAltres() {
        return tipusClientAltres;
    }

    /**
     * Executa l'operació `setTipusClientAltres`.
     *
     * @param tipusClientAltres paràmetre necessari per a l'operació.
     */
    public void setTipusClientAltres(String tipusClientAltres) {
        this.tipusClientAltres = tipusClientAltres;
    }

    /**
     * Executa l'operació `getAdreca`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getAdreca() {
        return adreca;
    }

    /**
     * Executa l'operació `setAdreca`.
     *
     * @param adreca paràmetre necessari per a l'operació.
     */
    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    /**
     * Executa l'operació `getTelefon`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getTelefon() {
        return telefon;
    }

    /**
     * Executa l'operació `setTelefon`.
     *
     * @param telefon paràmetre necessari per a l'operació.
     */
    public void setTelefon(String telefon) {
        this.telefon = telefon;
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
     * Executa l'operació `getNotes`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Executa l'operació `setNotes`.
     *
     * @param notes paràmetre necessari per a l'operació.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
