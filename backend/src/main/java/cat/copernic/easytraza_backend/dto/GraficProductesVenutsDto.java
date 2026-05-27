package cat.copernic.easytraza_backend.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO `GraficProductesVenutsDto` del projecte EasyTraza.
 */
public class GraficProductesVenutsDto {

    private final List<Integer> dies;
    private final List<Long> quantitats;
    private final Long totalUnitats;

    /**
     * Crea una nova instància del component.
     *
     * @param dies paràmetre necessari per a l'operació.
     * @param quantitats paràmetre necessari per a l'operació.
     * @param totalUnitats paràmetre necessari per a l'operació.
     */
    public GraficProductesVenutsDto(List<Integer> dies, List<Long> quantitats, Long totalUnitats) {
        this.dies = dies == null ? new ArrayList<>() : dies;
        this.quantitats = quantitats == null ? new ArrayList<>() : quantitats;
        this.totalUnitats = totalUnitats == null ? 0L : totalUnitats;
    }

    /**
     * Executa l'operació `getDies`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<Integer> getDies() {
        return dies;
    }

    /**
     * Executa l'operació `getQuantitats`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<Long> getQuantitats() {
        return quantitats;
    }

    /**
     * Executa l'operació `getTotalUnitats`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Long getTotalUnitats() {
        return totalUnitats;
    }
}
