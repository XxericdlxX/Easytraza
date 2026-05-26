package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.ComandaDto;
import cat.copernic.easytraza_backend.dto.LiniaComandaDto;
import cat.copernic.easytraza_backend.model.AlbaraClient;
import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.model.Comanda;
import cat.copernic.easytraza_backend.model.LiniaClient;
import cat.copernic.easytraza_backend.model.LiniaComanda;
import cat.copernic.easytraza_backend.model.Producte;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.EstatAlbaraClient;
import cat.copernic.easytraza_backend.model.enums.EstatComanda;
import cat.copernic.easytraza_backend.model.enums.EstatLiniaClient;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import cat.copernic.easytraza_backend.repository.ClientRepository;
import cat.copernic.easytraza_backend.repository.ComandaRepository;
import cat.copernic.easytraza_backend.repository.LotProveidorRepository;
import cat.copernic.easytraza_backend.repository.ProducteRepository;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servei que conté la lògica de negoci del CRUD de comandes.
 *
 * La comanda queda diferenciada de l'albarà de client: la comanda és el pedido
 * previst i l'albarà és el document de lliurament i traçabilitat. Aquest servei
 * permet generar un albarà de client a partir d'una comanda.
 */
@Service
public class ComandaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComandaService.class);

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProducteRepository producteRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private LotProveidorRepository lotProveidorRepository;

    @Autowired
    private AlbaraClientService albaraClientService;

    /**
     * Retorna totes les comandes.
     *
     * @return llista de comandes.
     */
    public List<Comanda> findAll() {
        return comandaRepository.findAll();
    }

    /**
     * Cerca una comanda per identificador.
     *
     * @param id identificador de la comanda.
     * @return comanda trobada, si existeix.
     */
    public Optional<Comanda> findById(Long id) {
        return comandaRepository.findById(id);
    }

    /**
     * Cerca comandes aplicant filtres combinats.
     *
     * @param clientNif filtre de client.
     * @param dataComanda filtre de data.
     * @param estat filtre d'estat.
     * @param producteId filtre de producte.
     * @return comandes trobades.
     */
    public List<Comanda> buscar(String clientNif, LocalDate dataComanda, EstatComanda estat, Long producteId) {
        String nifNormalitzat = normalitzarTextCerca(clientNif);
        return comandaRepository.buscar(nifNormalitzat.isBlank() ? null : nifNormalitzat, dataComanda, estat, producteId);
    }

    /**
     * Desa una comanda nova.
     *
     * @param comanda comanda a desar.
     * @return comanda desada.
     */
    public Comanda save(Comanda comanda) {
        try {
            prepararLinies(comanda);
            Comanda desada = comandaRepository.save(comanda);
            LOGGER.info("Comanda desada correctament amb id {}.", desada.getId());
            return desada;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en desar una comanda.", ex);
            throw ex;
        }
    }

    /**
     * Actualitza una comanda existent.
     *
     * @param id identificador de la comanda.
     * @param comandaActualitzada dades actualitzades.
     * @return comanda actualitzada.
     */
    @Transactional
    public Comanda update(Long id, Comanda comandaActualitzada) {
        Comanda existent = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("comandes.flash.no.trobada"));

        if (existent.getAlbaraClient() != null) {
            LOGGER.warn("No es pot editar la comanda {} perquè ja té un albarà generat.", id);
            throw new IllegalStateException("comandes.error.editar.amb.albara");
        }

        existent.setDataComanda(comandaActualitzada.getDataComanda());
        existent.setClient(comandaActualitzada.getClient());
        existent.setEstat(comandaActualitzada.getEstat() != null ? comandaActualitzada.getEstat() : EstatComanda.PENDENT);
        existent.setObservacions(comandaActualitzada.getObservacions());

        if (comandaActualitzada.getUsuariCreador() != null) {
            existent.setUsuariCreador(comandaActualitzada.getUsuariCreador());
        }

        existent.getLinies().clear();
        comandaRepository.flush();

        for (LiniaComanda linia : comandaActualitzada.getLinies()) {
            linia.setId(null);
            linia.setComanda(existent);
            existent.getLinies().add(linia);
        }

        try {
            prepararLinies(existent);
            Comanda desada = comandaRepository.saveAndFlush(existent);
            LOGGER.info("Comanda actualitzada correctament amb id {}.", desada.getId());
            return desada;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en actualitzar la comanda amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Elimina una comanda per identificador.
     *
     * @param id identificador de la comanda.
     */
    public void deleteById(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("comandes.flash.no.trobada"));

        if (comanda.getAlbaraClient() != null) {
            LOGGER.warn("No es pot eliminar la comanda {} perquè ja té un albarà generat.", id);
            throw new IllegalStateException("comandes.error.eliminar.amb.albara");
        }

        try {
            comandaRepository.deleteById(id);
            LOGGER.info("Comanda eliminada correctament amb id {}.", id);
        } catch (RuntimeException ex) {
            LOGGER.error("Error en eliminar la comanda amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Genera un albarà de client a partir d'una comanda.
     *
     * @param id identificador de la comanda.
     * @return comanda actualitzada amb l'albarà associat.
     */
    @Transactional
    public Comanda generarAlbaraClient(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("comandes.flash.no.trobada"));

        if (comanda.getAlbaraClient() != null) {
            LOGGER.warn("No s'ha generat albarà perquè la comanda {} ja en té un.", id);
            throw new IllegalStateException("comandes.error.albara.ja.generat");
        }

        if (comanda.getEstat() == EstatComanda.CANCELADA) {
            LOGGER.warn("No s'ha generat albarà perquè la comanda {} està cancel·lada.", id);
            throw new IllegalStateException("comandes.error.albara.cancelada");
        }

        if (lotProveidorRepository.findByEstat(EstatLot.OBERT).isEmpty()) {
            LOGGER.warn("No s'ha generat albarà de comanda {} perquè no hi ha lots oberts.", id);
            throw new IllegalStateException("comandes.error.albara.sense.lots.oberts");
        }

        if (comanda.getLinies() == null || comanda.getLinies().isEmpty()) {
            LOGGER.warn("No s'ha generat albarà de comanda {} perquè no té línies.", id);
            throw new IllegalStateException("comandes.linies.obligatories");
        }

        AlbaraClient albara = new AlbaraClient();
        albara.setDataProduccio(comanda.getDataComanda());
        albara.setEstat(EstatAlbaraClient.PENDENT_LLIURAR);
        albara.setClient(comanda.getClient());
        albara.setUsuariCreador(obtenirUsuariLoguejat() != null ? obtenirUsuariLoguejat() : comanda.getUsuariCreador());

        List<LiniaClient> liniesAlbara = new ArrayList<>();
        int numeroLotIntern = 1;

        for (LiniaComanda liniaComanda : comanda.getLinies()) {
            if (liniaComanda.getProducte() == null || liniaComanda.getQuantitat() == null || liniaComanda.getQuantitat() <= 0) {
                LOGGER.warn("No s'ha generat albarà de comanda {} perquè una línia no és vàlida.", id);
                throw new IllegalStateException("comandes.linies.obligatories");
            }

            LiniaClient liniaClient = new LiniaClient();
            liniaClient.setNumeroLotIntern(numeroLotIntern++);
            liniaClient.setQuantitat(liniaComanda.getQuantitat());
            liniaClient.setEstat(EstatLiniaClient.SENSE_LLIURAR);
            liniaClient.setProducte(liniaComanda.getProducte());
            liniaClient.setOperari(albara.getUsuariCreador());
            liniaClient.setAlbaraClient(albara);
            liniesAlbara.add(liniaClient);
        }

        albara.setLinies(liniesAlbara);
        AlbaraClient albaraDesat = albaraClientService.save(albara);

        comanda.setAlbaraClient(albaraDesat);
        comanda.setEstat(EstatComanda.ALBARA_GENERAT);

        Comanda actualitzada = comandaRepository.save(comanda);
        LOGGER.info("Generat albarà de client {} a partir de la comanda {}.", albaraDesat.getId(), id);

        return actualitzada;
    }

    /**
     * Valida una comanda abans de crear-la o editar-la.
     *
     * @param dto dades del formulari.
     * @return clau de missatge d'error o null si és vàlida.
     */
    public String validarComanda(ComandaDto dto) {
        if (dto.getDataComanda() == null) {
            LOGGER.warn("Validació de comanda rebutjada perquè falta la data.");
            return "comandes.data.obligatoria";
        }

        if (dto.getClientNif() == null || dto.getClientNif().isBlank()) {
            LOGGER.warn("Validació de comanda rebutjada perquè falta el client.");
            return "comandes.client.obligatori";
        }

        if (clientRepository.findById(normalitzarDocument(dto.getClientNif())).isEmpty()) {
            LOGGER.warn("Validació de comanda rebutjada perquè el client no existeix.");
            return "comandes.error.client.no.trobat";
        }

        List<LiniaComandaDto> liniesValides = obtenirLiniesValides(dto.getLinies());
        if (liniesValides.isEmpty()) {
            LOGGER.warn("Validació de comanda rebutjada perquè no conté línies vàlides.");
            return "comandes.linies.obligatories";
        }

        for (LiniaComandaDto linia : liniesValides) {
            if (linia.getProducteId() == null) {
                return "comandes.linia.producte.obligatori";
            }

            if (producteRepository.findById(linia.getProducteId()).isEmpty()) {
                return "comandes.error.producte.no.trobat";
            }

            if (linia.getQuantitat() == null || linia.getQuantitat() <= 0) {
                return "comandes.linia.quantitat.min";
            }
        }

        return null;
    }

    /**
     * Converteix un DTO en entitat.
     *
     * @param dto dades del formulari.
     * @return entitat de comanda.
     */
    public Comanda convertirDtoAEntity(ComandaDto dto) {
        Client client = clientRepository.findById(normalitzarDocument(dto.getClientNif())).orElse(null);
        Usuari usuariLoguejat = obtenirUsuariLoguejat();

        Comanda comanda = new Comanda();
        comanda.setId(dto.getId());
        comanda.setDataComanda(dto.getDataComanda());
        comanda.setClient(client);
        comanda.setEstat(dto.getEstat() != null ? dto.getEstat() : EstatComanda.PENDENT);
        comanda.setObservacions(normalitzarObservacions(dto.getObservacions()));
        comanda.setUsuariCreador(usuariLoguejat);

        List<LiniaComanda> linies = new ArrayList<>();
        for (LiniaComandaDto liniaDto : obtenirLiniesValides(dto.getLinies())) {
            Producte producte = producteRepository.findById(liniaDto.getProducteId()).orElse(null);

            LiniaComanda linia = new LiniaComanda();
            linia.setId(liniaDto.getId());
            linia.setProducte(producte);
            linia.setQuantitat(liniaDto.getQuantitat());
            linia.setObservacions(normalitzarObservacions(liniaDto.getObservacions()));
            linia.setComanda(comanda);
            linies.add(linia);
        }

        comanda.setLinies(linies);
        return comanda;
    }

    /**
     * Converteix una entitat en DTO.
     *
     * @param entity entitat de comanda.
     * @return DTO de comanda.
     */
    public ComandaDto convertirEntityADto(Comanda entity) {
        ComandaDto dto = new ComandaDto();
        dto.setId(entity.getId());
        dto.setDataComanda(entity.getDataComanda());
        dto.setEstat(entity.getEstat());
        dto.setObservacions(entity.getObservacions());

        if (entity.getClient() != null) {
            dto.setClientNif(entity.getClient().getNif());
        }

        if (entity.getUsuariCreador() != null) {
            String nom = entity.getUsuariCreador().getNom() != null ? entity.getUsuariCreador().getNom() : "";
            String cognoms = entity.getUsuariCreador().getCognoms() != null ? entity.getUsuariCreador().getCognoms() : "";
            dto.setUsuariCreadorNom((nom + " " + cognoms).trim());
        }

        List<LiniaComandaDto> liniesDto = new ArrayList<>();
        if (entity.getLinies() != null) {
            for (LiniaComanda linia : entity.getLinies()) {
                LiniaComandaDto liniaDto = new LiniaComandaDto();
                liniaDto.setId(linia.getId());
                liniaDto.setQuantitat(linia.getQuantitat());
                liniaDto.setObservacions(linia.getObservacions());

                if (linia.getProducte() != null) {
                    liniaDto.setProducteId(linia.getProducte().getId());
                }

                liniesDto.add(liniaDto);
            }
        }

        if (liniesDto.isEmpty()) {
            liniesDto.add(new LiniaComandaDto());
        }

        dto.setLinies(liniesDto);
        return dto;
    }

    /**
     * Assegura que el formulari tingui com a mínim una línia visible.
     *
     * @param dto dades del formulari.
     */
    public void assegurarMinimUnaLinia(ComandaDto dto) {
        if (dto.getLinies() == null) {
            dto.setLinies(new ArrayList<>());
        }

        if (dto.getLinies().isEmpty()) {
            dto.getLinies().add(new LiniaComandaDto());
        }
    }

    private void prepararLinies(Comanda comanda) {
        for (LiniaComanda linia : comanda.getLinies()) {
            linia.setComanda(comanda);
        }
    }

    private List<LiniaComandaDto> obtenirLiniesValides(List<LiniaComandaDto> linies) {
        List<LiniaComandaDto> valides = new ArrayList<>();

        if (linies == null) {
            return valides;
        }

        for (LiniaComandaDto linia : linies) {
            boolean teProducte = linia.getProducteId() != null;
            boolean teQuantitat = linia.getQuantitat() != null;

            if (teProducte || teQuantitat || (linia.getObservacions() != null && !linia.getObservacions().isBlank())) {
                if (!teProducte || linia.getQuantitat() == null || linia.getQuantitat() <= 0) {
                    continue;
                }

                valides.add(linia);
            }
        }

        return valides;
    }

    private Usuari obtenirUsuariLoguejat() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            String email = authentication.getName();
            return usuariRepository.findByEmailIgnoreCase(email).orElse(null);
        }

        return null;
    }

    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }

    private String normalitzarDocument(String document) {
        if (document == null) {
            return null;
        }

        return document.trim().toUpperCase().replace(" ", "");
    }

    private String normalitzarObservacions(String observacions) {
        return observacions == null || observacions.isBlank() ? null : observacions.trim();
    }
}
