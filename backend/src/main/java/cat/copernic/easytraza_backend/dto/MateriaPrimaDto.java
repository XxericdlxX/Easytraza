package cat.copernic.easytraza_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO `MateriaPrimaDto` del projecte EasyTraza.
 */
public class MateriaPrimaDto {

    private Long id;

    @NotBlank(message = "{materies.nom.obligatori}")
    @Size(max = 100, message = "{materies.nom.max}")
    private String nom;

    @NotBlank(message = "{materies.descripcio.obligatoria}")
    @Size(max = 255, message = "{materies.descripcio.max}")
    private String descripcio;

    /**
     * Crea una nova instància del component.
     */
    public MateriaPrimaDto() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param nom paràmetre necessari per a l'operació.
     * @param descripcio paràmetre necessari per a l'operació.
     */
    public MateriaPrimaDto(Long id, String nom, String descripcio) {
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
