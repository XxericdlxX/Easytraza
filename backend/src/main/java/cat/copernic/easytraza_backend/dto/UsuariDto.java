package cat.copernic.easytraza_backend.dto;

import cat.copernic.easytraza_backend.model.enums.Rol;

public class UsuariDto {

    private Long id;
    private String nom;
    private String cognoms;
    private Rol rol;
    private String email;

    public UsuariDto() {
    }

    public UsuariDto(Long id, String nom, String cognoms, Rol rol, String email) {
        this.id = id;
        this.nom = nom;
        this.cognoms = cognoms;
        this.rol = rol;
        this.email = email;
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
}
