package cat.copernic.easytraza_backend.dto;

/**
 * DTO `MesGraficProductesDto` del projecte EasyTraza.
 */
public class MesGraficProductesDto {

    private final String valor;
    private final String etiqueta;

    /**
     * Crea una nova instància del component.
     *
     * @param valor paràmetre necessari per a l'operació.
     * @param etiqueta paràmetre necessari per a l'operació.
     */
    public MesGraficProductesDto(String valor, String etiqueta) {
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    /**
     * Executa l'operació `getValor`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getValor() {
        return valor;
    }

    /**
     * Executa l'operació `getEtiqueta`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getEtiqueta() {
        return etiqueta;
    }
}
