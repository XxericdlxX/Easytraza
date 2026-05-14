package cat.copernic.easytraza_backend.dto.mobile;

public class MobileUsuariDto {

    private Long id;
    private String nom;
    private String cognoms;
    private String email;
    private String rol;

    public MobileUsuariDto() {
    }

    public MobileUsuariDto(Long id, String nom, String cognoms, String email, String rol) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.email = email;
        this.rol = rol;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
