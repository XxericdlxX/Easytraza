package cat.copernic.easytraza_backend.dto.mobile;

/**
 * DTO `MobileUsuariDto` del projecte EasyTraza.
 */
public class MobileUsuariDto {

    private Long id;
    private String nom;
    private String cognoms;
    private String rol;
    private String fotoPerfilUrl;

    /**
     * Crea una nova instància del component.
     */
    public MobileUsuariDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param cognoms paràmetre necessari per a l'operació.
     * @param rol paràmetre necessari per a l'operació.
     * @param fotoPerfilUrl paràmetre necessari per a l'operació.
     */
    public MobileUsuariDto(Long id, String nom, String cognoms, String rol, String fotoPerfilUrl) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.rol = rol;
        this.fotoPerfilUrl = fotoPerfilUrl;
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
    public String getRol() {
        return rol;
    }

    /**
     * Executa l'operació `setRol`.
     *
     * @param rol paràmetre necessari per a l'operació.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Executa l'operació `getFotoPerfilUrl`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    /**
     * Executa l'operació `setFotoPerfilUrl`.
     *
     * @param fotoPerfilUrl paràmetre necessari per a l'operació.
     */
    public void setFotoPerfilUrl(String fotoPerfilUrl) {
        this.fotoPerfilUrl = fotoPerfilUrl;
    }
}
