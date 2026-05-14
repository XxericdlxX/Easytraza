package cat.copernic.easytraza_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class GraficProductesVenutsDto {

    private final List<Integer> dies;
    private final List<Long> quantitats;
    private final Long totalUnitats;

    public GraficProductesVenutsDto(List<Integer> dies, List<Long> quantitats, Long totalUnitats) {
        this.dies = dies == null ? new ArrayList<>() : dies;
        this.quantitats = quantitats == null ? new ArrayList<>() : quantitats;
        this.totalUnitats = totalUnitats == null ? 0L : totalUnitats;
    }

    public List<Integer> getDies() {
        return dies;
    }

    public List<Long> getQuantitats() {
        return quantitats;
    }

    public Long getTotalUnitats() {
        return totalUnitats;
    }
}
