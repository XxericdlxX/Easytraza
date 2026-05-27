package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.GraficProductesVenutsDto;
import cat.copernic.easytraza_backend.dto.MesGraficProductesDto;
import cat.copernic.easytraza_backend.model.enums.EstatAlbaraClient;
import cat.copernic.easytraza_backend.repository.AlbaraClientRepository;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei `GraficProductesVenutsService` del projecte EasyTraza.
 */
@Service
public class GraficProductesVenutsService {

    @Autowired
    private AlbaraClientRepository albaraClientRepository;

    /**
     * Executa l'operació `resoldreMesSeleccionat`.
     *
     * @param mes paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public YearMonth resoldreMesSeleccionat(String mes) {
        if (mes == null || mes.isBlank()) {
            return YearMonth.now();
        }

        try {
            return YearMonth.parse(mes, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException ex) {
            return YearMonth.now();
        }
    }

    /**
     * Executa l'operació `obtenirUltimsDotzeMesos`.
     *
     * @param locale paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<MesGraficProductesDto> obtenirUltimsDotzeMesos(Locale locale) {
        Locale localeResolut = locale == null ? Locale.forLanguageTag("es") : locale;
        YearMonth mesActual = YearMonth.now();
        List<MesGraficProductesDto> mesos = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            YearMonth mes = mesActual.minusMonths(i);
            String nomMes = mes.getMonth().getDisplayName(TextStyle.FULL, localeResolut);
            String etiqueta = capitalitzar(nomMes) + " " + mes.getYear();
            mesos.add(new MesGraficProductesDto(mes.toString(), etiqueta));
        }

        return mesos;
    }

    /**
     * Executa l'operació `obtenirGraficMensual`.
     *
     * @param mes paràmetre necessari per a l'operació.
     * @param producteId paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public GraficProductesVenutsDto obtenirGraficMensual(YearMonth mes, Long producteId) {
        YearMonth mesResolut = mes == null ? YearMonth.now() : mes;
        Map<Integer, Long> quantitatsPerDia = inicialitzarDiesMes(mesResolut);

        List<Object[]> resultats = albaraClientRepository.findQuantitatsVenudesAgrupadesPerDia(
                EstatAlbaraClient.LLIURAT,
                mesResolut.atDay(1),
                mesResolut.atEndOfMonth(),
                producteId
        );

        for (Object[] resultat : resultats) {
            int dia = convertirAEnter(resultat[0]);
            long quantitat = convertirALong(resultat[1]);

            if (quantitatsPerDia.containsKey(dia)) {
                quantitatsPerDia.put(dia, quantitat);
            }
        }

        List<Integer> dies = new ArrayList<>(quantitatsPerDia.keySet());
        List<Long> quantitats = new ArrayList<>(quantitatsPerDia.values());
        long totalUnitats = quantitats.stream().mapToLong(Long::longValue).sum();

        return new GraficProductesVenutsDto(dies, quantitats, totalUnitats);
    }

    /**
     * Executa l'operació `inicialitzarDiesMes`.
     *
     * @param mes paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private Map<Integer, Long> inicialitzarDiesMes(YearMonth mes) {
        Map<Integer, Long> dies = new LinkedHashMap<>();

        for (int dia = 1; dia <= mes.lengthOfMonth(); dia++) {
            dies.put(dia, 0L);
        }

        return dies;
    }

    /**
     * Executa l'operació `convertirAEnter`.
     *
     * @param valor paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private int convertirAEnter(Object valor) {
        return valor instanceof Number number ? number.intValue() : 0;
    }

    /**
     * Executa l'operació `convertirALong`.
     *
     * @param valor paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private long convertirALong(Object valor) {
        return valor instanceof Number number ? number.longValue() : 0L;
    }

    /**
     * Executa l'operació `capitalitzar`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String capitalitzar(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
