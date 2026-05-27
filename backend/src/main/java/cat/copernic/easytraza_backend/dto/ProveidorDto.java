package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO `ProveidorDto` del projecte EasyTraza.
 */
public class ProveidorDto {

    @NotBlank(message = "{proveidors.cif.obligatori}")
    @Size(max = 20, message = "{proveidors.cif.max}")
    private String cif;

    @NotBlank(message = "{proveidors.nom.obligatori}")
    @Size(max = 100, message = "{proveidors.nom.max}")
    private String nom;

    @NotBlank(message = "{proveidors.adreca.obligatoria}")
    @Size(max = 255, message = "{proveidors.adreca.max}")
    private String adreca;

    @Size(max = 255, message = "{proveidors.notes.max}")
    private String notes;

    @Size(max = 20, message = "{proveidors.telefon.max}")
    @Pattern(
            regexp = "^$|^[+0-9 ()-]{7,20}$",
            message = "{proveidors.telefon.invalid}"
    )
    private String telefon;

    @Size(max = 100, message = "{proveidors.email.max}")
    @Email(message = "{proveidors.email.invalid}")
    private String email;

    /**
     * Crea una nova instància del component.
     */
    public ProveidorDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param cif paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param adreca paràmetre necessari per a l'operació.
     * @param notes paràmetre necessari per a l'operació.
     * @param telefon paràmetre necessari per a l'operació.
     * @param email paràmetre necessari per a l'operació.
     */
    public ProveidorDto(String cif, String nom, String adreca, String notes, String telefon, String email) {
        this.cif = cif;
        this.nom = nom;
        this.adreca = adreca;
        this.notes = notes;
        this.telefon = telefon;
        this.email = email;
    }

    /**
     * Executa l'operació `getCif`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getCif() {
        return cif;
    }

    /**
     * Executa l'operació `setCif`.
     *
     * @param cif paràmetre necessari per a l'operació.
     */
    public void setCif(String cif) {
        this.cif = cif;
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
}
