package cat.copernic.easytraza_backend.model;

import cat.copernic.easytraza_backend.model.enums.EstatLot;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entitat del model `LotProveidor` del projecte EasyTraza.
 */
@Entity
@Table(name = "lots_proveidor")
public class LotProveidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codi_lot", nullable = false, length = 100)
    private String codiLot;

    @Column(name = "codi_materia_prima_ocr", length = 120)
    private String codiMateriaPrimaOcr;

    @Column(nullable = false)
    private Double quantitat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstatLot estat;

    private LocalDate dataObertura;

    private LocalDate dataAcabament;

    @ManyToOne(optional = false)
    @JoinColumn(name = "materia_prima_id", nullable = false)
    private MateriaPrima materiaPrima;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proveidor_cif", nullable = false)
    private Proveidor proveidor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "albara_proveidor_id", nullable = false)
    private AlbaraProveidor albaraProveidor;

    /**
     * Crea una nova instància del component.
     */
    public LotProveidor() {
    }

    /**
     * Crea una nova instància del component.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param codiLot paràmetre necessari per a l'operació.
     * @param quantitat paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @param dataObertura paràmetre necessari per a l'operació.
     * @param dataAcabament paràmetre necessari per a l'operació.
     * @param materiaPrima paràmetre necessari per a l'operació.
     * @param proveidor paràmetre necessari per a l'operació.
     * @param albaraProveidor paràmetre necessari per a l'operació.
     */
    public LotProveidor(Long id, String codiLot, Double quantitat, EstatLot estat,
            LocalDate dataObertura, LocalDate dataAcabament,
            MateriaPrima materiaPrima, Proveidor proveidor,
            AlbaraProveidor albaraProveidor) {
        this.id = id;
        this.codiLot = codiLot;
        this.quantitat = quantitat;
        this.estat = estat;
        this.dataObertura = dataObertura;
        this.dataAcabament = dataAcabament;
        this.materiaPrima = materiaPrima;
        this.proveidor = proveidor;
        this.albaraProveidor = albaraProveidor;
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
     * Executa l'operació `getCodiLot`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getCodiLot() {
        return codiLot;
    }

    /**
     * Executa l'operació `setCodiLot`.
     *
     * @param codiLot paràmetre necessari per a l'operació.
     */
    public void setCodiLot(String codiLot) {
        this.codiLot = codiLot;
    }

    /**
     * Executa l'operació `getCodiMateriaPrimaOcr`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public String getCodiMateriaPrimaOcr() {
        return codiMateriaPrimaOcr;
    }

    /**
     * Executa l'operació `setCodiMateriaPrimaOcr`.
     *
     * @param codiMateriaPrimaOcr paràmetre necessari per a l'operació.
     */
    public void setCodiMateriaPrimaOcr(String codiMateriaPrimaOcr) {
        this.codiMateriaPrimaOcr = codiMateriaPrimaOcr;
    }

    /**
     * Executa l'operació `getQuantitat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Double getQuantitat() {
        return quantitat;
    }

    /**
     * Executa l'operació `setQuantitat`.
     *
     * @param quantitat paràmetre necessari per a l'operació.
     */
    public void setQuantitat(Double quantitat) {
        this.quantitat = quantitat;
    }

    /**
     * Executa l'operació `getEstat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public EstatLot getEstat() {
        return estat;
    }

    /**
     * Executa l'operació `setEstat`.
     *
     * @param estat paràmetre necessari per a l'operació.
     */
    public void setEstat(EstatLot estat) {
        this.estat = estat;
    }

    /**
     * Executa l'operació `getDataObertura`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public LocalDate getDataObertura() {
        return dataObertura;
    }

    /**
     * Executa l'operació `setDataObertura`.
     *
     * @param dataObertura paràmetre necessari per a l'operació.
     */
    public void setDataObertura(LocalDate dataObertura) {
        this.dataObertura = dataObertura;
    }

    /**
     * Executa l'operació `getDataAcabament`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public LocalDate getDataAcabament() {
        return dataAcabament;
    }

    /**
     * Executa l'operació `setDataAcabament`.
     *
     * @param dataAcabament paràmetre necessari per a l'operació.
     */
    public void setDataAcabament(LocalDate dataAcabament) {
        this.dataAcabament = dataAcabament;
    }

    /**
     * Executa l'operació `getMateriaPrima`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public MateriaPrima getMateriaPrima() {
        return materiaPrima;
    }

    /**
     * Executa l'operació `setMateriaPrima`.
     *
     * @param materiaPrima paràmetre necessari per a l'operació.
     */
    public void setMateriaPrima(MateriaPrima materiaPrima) {
        this.materiaPrima = materiaPrima;
    }

    /**
     * Executa l'operació `getProveidor`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public Proveidor getProveidor() {
        return proveidor;
    }

    /**
     * Executa l'operació `setProveidor`.
     *
     * @param proveidor paràmetre necessari per a l'operació.
     */
    public void setProveidor(Proveidor proveidor) {
        this.proveidor = proveidor;
    }

    /**
     * Executa l'operació `getAlbaraProveidor`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public AlbaraProveidor getAlbaraProveidor() {
        return albaraProveidor;
    }

    /**
     * Executa l'operació `setAlbaraProveidor`.
     *
     * @param albaraProveidor paràmetre necessari per a l'operació.
     */
    public void setAlbaraProveidor(AlbaraProveidor albaraProveidor) {
        this.albaraProveidor = albaraProveidor;
    }
}
