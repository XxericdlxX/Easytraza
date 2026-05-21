package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO `ProducteDto` del projecte EasyTraza.
 */
public class ProducteDto {

    private Long id;

    @NotBlank(message = "{productes.nom.obligatori}")
    @Size(max = 100, message = "{productes.nom.max}")
    private String nom;

    @Size(max = 255, message = "{productes.descripcio.max}")
    private String descripcio;

    /**
     * Crea una nova instància del component.
     */
    public ProducteDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param descripcio paràmetre necessari per a l'operació.
     */
    public ProducteDto(Long id, String nom, String descripcio) {
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
