package cat.copernic.easytraza_backend.dto;

public class MesGraficProductesDto {

    private final String valor;
    private final String etiqueta;

    public MesGraficProductesDto(String valor, String etiqueta) {
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    public String getValor() {
        return valor;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
