package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.AlbaraClientDto;
import cat.copernic.easytraza_backend.dto.LiniaClientDto;
import cat.copernic.easytraza_backend.model.AlbaraClient;
import cat.copernic.easytraza_backend.model.Client;
import cat.copernic.easytraza_backend.model.LiniaClient;
import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.Producte;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.EstatAlbaraClient;
import cat.copernic.easytraza_backend.model.enums.EstatLiniaClient;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import cat.copernic.easytraza_backend.repository.AlbaraClientRepository;
import cat.copernic.easytraza_backend.repository.ClientRepository;
import cat.copernic.easytraza_backend.repository.LotProveidorRepository;
import cat.copernic.easytraza_backend.repository.ProducteRepository;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei `AlbaraClientService` del projecte EasyTraza.
 */
@Service
public class AlbaraClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.albarans.client");

    @Autowired
    private AlbaraClientRepository albaraClientRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProducteRepository producteRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private LotProveidorRepository lotProveidorRepository;

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<AlbaraClient> findAll() {
        return albaraClientRepository.findAll();
    }

    /**
     * Executa l'operació `findById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Optional<AlbaraClient> findById(Long id) {
        return albaraClientRepository.findById(id);
    }

    /**
     * Executa l'operació `buscar`.
     *
     * @param clientNif paràmetre necessari per a l'operació.
     * @param dataProduccio paràmetre necessari per a l'operació.
     * @param estat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<AlbaraClient> buscar(String clientNif, LocalDate dataProduccio, EstatAlbaraClient estat) {
        String nifNormalitzat = normalitzarTextCerca(clientNif);

        if (nifNormalitzat.isEmpty() && dataProduccio == null && estat == null) {
            return findAll();
        }

        if (!nifNormalitzat.isEmpty() && dataProduccio != null && estat != null) {
            return albaraClientRepository.findByClient_NifContainingIgnoreCaseAndDataProduccioAndEstat(
                    nifNormalitzat,
                    dataProduccio,
                    estat
            );
        }

        if (!nifNormalitzat.isEmpty() && dataProduccio != null) {
            return albaraClientRepository.findByClient_NifContainingIgnoreCaseAndDataProduccio(
                    nifNormalitzat,
                    dataProduccio
            );
        }

        if (!nifNormalitzat.isEmpty() && estat != null) {
            return albaraClientRepository.findByClient_NifContainingIgnoreCaseAndEstat(nifNormalitzat, estat);
        }

        if (dataProduccio != null && estat != null) {
            return albaraClientRepository.findByDataProduccioAndEstat(dataProduccio, estat);
        }

        if (!nifNormalitzat.isEmpty()) {
            return albaraClientRepository.findByClient_NifContainingIgnoreCase(nifNormalitzat);
        }

        if (dataProduccio != null) {
            return albaraClientRepository.findByDataProduccio(dataProduccio);
        }

        return albaraClientRepository.findByEstat(estat);
    }

    /**
     * Executa l'operació `save`.
     *
     * @param albaraClient paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public AlbaraClient save(AlbaraClient albaraClient) {
        try {
            prepararLinies(albaraClient);
            AlbaraClient albaraDesat = albaraClientRepository.save(albaraClient);
            LOGGER.info("Albarà de client desat correctament amb id {}.", albaraDesat.getId());
            return albaraDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en desar un albarà de client.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `update`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param albaraActualitzat paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Transactional
    public AlbaraClient update(Long id, AlbaraClient albaraActualitzat) {
        Optional<AlbaraClient> existentOpt = albaraClientRepository.findById(id);

        if (existentOpt.isEmpty()) {
            LOGGER.warn("No s'ha pogut actualitzar l'albarà de client perquè no existeix. Id: {}", id);
            return null;
        }

        AlbaraClient existent = existentOpt.get();
        validarEditable(existent);

        existent.setDataProduccio(albaraActualitzat.getDataProduccio());
        existent.setClient(albaraActualitzat.getClient());

        if (albaraActualitzat.getUsuariCreador() != null) {
            existent.setUsuariCreador(albaraActualitzat.getUsuariCreador());
        }

        existent.getLinies().clear();
        albaraClientRepository.flush();

        for (LiniaClient linia : albaraActualitzat.getLinies()) {
            linia.setId(null);
            linia.setAlbaraClient(existent);
            existent.getLinies().add(linia);
        }

        try {
            prepararLinies(existent);
            AlbaraClient albaraDesat = albaraClientRepository.saveAndFlush(existent);
            LOGGER.info("Albarà de client actualitzat correctament amb id {}.", albaraDesat.getId());
            return albaraDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en actualitzar l'albarà de client amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `deleteById`.
     *
     * @param id paràmetre necessari per a l'operació.
     */
    public void deleteById(Long id) {
        Optional<AlbaraClient> albaraOpt = albaraClientRepository.findById(id);

        if (albaraOpt.isPresent()) {
            validarEditable(albaraOpt.get());
        }

        try {
            albaraClientRepository.deleteById(id);
            LOGGER.info("Albarà de client eliminat correctament amb id {}.", id);
        } catch (RuntimeException ex) {
            LOGGER.error("Error en eliminar l'albarà de client amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `marcarComLliurat`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Transactional
    public AlbaraClient marcarComLliurat(Long id) {
        AlbaraClient albara = albaraClientRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("No s'ha pogut marcar com a lliurat l'albarà de client perquè no existeix. Id: {}", id);
                    return new IllegalArgumentException("albara.client.flash.no.trobat");
                });

        if (albara.getEstat() == EstatAlbaraClient.LLIURAT) {
            LOGGER.warn("Intent de lliurar un albarà de client que ja estava lliurat. Id: {}", id);
            throw new IllegalStateException("albara.client.error.ja.lliurat");
        }

        albara.setEstat(EstatAlbaraClient.LLIURAT);

        for (LiniaClient linia : albara.getLinies()) {
            linia.setEstat(EstatLiniaClient.LLIURADA);
        }

        try {
            AlbaraClient albaraDesat = albaraClientRepository.save(albara);
            LOGGER.info("Albarà de client marcat com a lliurat correctament amb id {}.", id);
            return albaraDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en marcar com a lliurat l'albarà de client amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `validarAlbara`.
     *
     * @param dto paràmetre necessari per a l'operació.
     * @param idActual paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public String validarAlbara(AlbaraClientDto dto, Long idActual) {
        if (dto.getDataProduccio() == null) {
            LOGGER.warn("Validació d'albarà de client rebutjada perquè falta la data de producció.");
            return "albara.client.data.obligatoria";
        }

        if (dto.getClientNif() == null || dto.getClientNif().isBlank()) {
            LOGGER.warn("Validació d'albarà de client rebutjada perquè falta el client.");
            return "albara.client.client.obligatori";
        }

        String nifNormalitzat = normalitzarDocument(dto.getClientNif());
        if (clientRepository.findById(nifNormalitzat).isEmpty()) {
            LOGGER.warn("Validació d'albarà de client rebutjada perquè el client no existeix.");
            return "albara.client.error.client.no.trobat";
        }

        if (!existeixLotObert()) {
            LOGGER.warn("Validació d'albarà de client rebutjada perquè no hi ha lots oberts.");
            return "albara.client.error.sense.lots.oberts";
        }

        List<LiniaClientDto> liniesValides = obtenirLiniesValides(dto.getLinies());
        if (liniesValides.isEmpty()) {
            LOGGER.warn("Validació d'albarà de client rebutjada perquè no conté línies vàlides.");
            return "albara.client.linies.obligatories";
        }

        for (LiniaClientDto linia : liniesValides) {
            if (linia.getProducteId() == null) {
                LOGGER.warn("Validació d'albarà de client rebutjada perquè una línia no té producte.");
                return "albara.client.linia.producte.obligatori";
            }

            if (producteRepository.findById(linia.getProducteId()).isEmpty()) {
                LOGGER.warn("Validació d'albarà de client rebutjada perquè un producte no existeix.");
                return "albara.client.error.producte.no.trobat";
            }

            if (linia.getQuantitat() == null || linia.getQuantitat() <= 0) {
                LOGGER.warn("Validació d'albarà de client rebutjada perquè una línia té quantitat no vàlida.");
                return "albara.client.linia.quantitat.min";
            }
        }

        if (idActual != null) {
            Optional<AlbaraClient> existent = albaraClientRepository.findById(idActual);

            if (existent.isPresent() && existent.get().getEstat() == EstatAlbaraClient.LLIURAT) {
                LOGGER.warn("Validació d'albarà de client rebutjada perquè ja està lliurat. Id: {}", idActual);
                return "albara.client.error.modificar.lliurat";
            }
        }

        return null;
    }

    /**
     * Executa l'operació `convertirDtoAEntity`.
     *
     * @param dto paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public AlbaraClient convertirDtoAEntity(AlbaraClientDto dto) {
        Client client = clientRepository.findById(normalitzarDocument(dto.getClientNif())).orElse(null);
        Usuari usuariLoguejat = obtenirUsuariLoguejat();

        AlbaraClient albara = new AlbaraClient();
        albara.setId(dto.getId());
        albara.setDataProduccio(dto.getDataProduccio());
        albara.setEstat(EstatAlbaraClient.PENDENT_LLIURAR);
        albara.setClient(client);
        albara.setUsuariCreador(usuariLoguejat);

        List<LiniaClient> linies = new ArrayList<>();
        int numeroLotIntern = 1;

        for (LiniaClientDto liniaDto : obtenirLiniesValides(dto.getLinies())) {
            Producte producte = producteRepository.findById(liniaDto.getProducteId()).orElse(null);

            LiniaClient linia = new LiniaClient();
            linia.setId(liniaDto.getId());
            linia.setNumeroLotIntern(numeroLotIntern++);
            linia.setQuantitat(liniaDto.getQuantitat());
            linia.setEstat(EstatLiniaClient.SENSE_LLIURAR);
            linia.setProducte(producte);
            linia.setOperari(usuariLoguejat);
            linia.setAlbaraClient(albara);

            linies.add(linia);
        }

        albara.setLinies(linies);
        return albara;
    }

    /**
     * Executa l'operació `convertirEntityADto`.
     *
     * @param entity paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public AlbaraClientDto convertirEntityADto(AlbaraClient entity) {
        AlbaraClientDto dto = new AlbaraClientDto();
        dto.setId(entity.getId());
        dto.setDataProduccio(entity.getDataProduccio());

        if (entity.getClient() != null) {
            dto.setClientNif(entity.getClient().getNif());
        }

        if (entity.getUsuariCreador() != null) {
            String nom = entity.getUsuariCreador().getNom() != null ? entity.getUsuariCreador().getNom() : "";
            String cognoms = entity.getUsuariCreador().getCognoms() != null ? entity.getUsuariCreador().getCognoms() : "";
            dto.setUsuariCreadorNom((nom + " " + cognoms).trim());
        }

        List<LiniaClientDto> liniesDto = new ArrayList<>();

        if (entity.getLinies() != null) {
            for (LiniaClient linia : entity.getLinies()) {
                LiniaClientDto liniaDto = new LiniaClientDto();
                liniaDto.setId(linia.getId());
                liniaDto.setNumeroLotIntern(linia.getNumeroLotIntern());
                liniaDto.setQuantitat(linia.getQuantitat());

                if (linia.getProducte() != null) {
                    liniaDto.setProducteId(linia.getProducte().getId());
                }

                liniesDto.add(liniaDto);
            }
        }

        if (liniesDto.isEmpty()) {
            liniesDto.add(new LiniaClientDto());
        }

        dto.setLinies(liniesDto);
        return dto;
    }

    /**
     * Executa l'operació `assegurarMinimUnaLinia`.
     *
     * @param dto paràmetre necessari per a l'operació.
     */
    public void assegurarMinimUnaLinia(AlbaraClientDto dto) {
        if (dto.getLinies() == null) {
            dto.setLinies(new ArrayList<>());
        }

        if (dto.getLinies().isEmpty()) {
            dto.getLinies().add(new LiniaClientDto());
        }
    }

    /**
     * Executa l'operació `prepararLinies`.
     *
     * @param albara paràmetre necessari per a l'operació.
     */
    private void prepararLinies(AlbaraClient albara) {
        List<LotProveidor> lotsOberts = obtenirLotsOberts();
        int numeroLotIntern = 1;

        for (LiniaClient linia : albara.getLinies()) {
            linia.setAlbaraClient(albara);
            linia.setNumeroLotIntern(numeroLotIntern++);

            if (linia.getEstat() == null) {
                linia.setEstat(EstatLiniaClient.SENSE_LLIURAR);
            }

            if (linia.getOperari() == null) {
                linia.setOperari(albara.getUsuariCreador());
            }

            linia.setLotsAssociats(new LinkedHashSet<>(lotsOberts));
        }
    }

    /**
     * Executa l'operació `existeixLotObert`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    private boolean existeixLotObert() {
        return !obtenirLotsOberts().isEmpty();
    }

    /**
     * Executa l'operació `obtenirLotsOberts`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    private List<LotProveidor> obtenirLotsOberts() {
        return lotProveidorRepository.findByEstat(EstatLot.OBERT);
    }

    /**
     * Executa l'operació `obtenirLiniesValides`.
     *
     * @param linies paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private List<LiniaClientDto> obtenirLiniesValides(List<LiniaClientDto> linies) {
        List<LiniaClientDto> valides = new ArrayList<>();

        if (linies == null) {
            return valides;
        }

        for (LiniaClientDto linia : linies) {
            boolean teProducte = linia.getProducteId() != null;
            boolean teQuantitat = linia.getQuantitat() != null;

            if (teProducte || teQuantitat) {
                if (!teProducte) {
                    continue;
                }

                if (linia.getQuantitat() == null || linia.getQuantitat() <= 0) {
                    continue;
                }

                valides.add(linia);
            }
        }

        return valides;
    }

    /**
     * Executa l'operació `validarEditable`.
     *
     * @param albara paràmetre necessari per a l'operació.
     */
    private void validarEditable(AlbaraClient albara) {
        if (albara.getEstat() == EstatAlbaraClient.LLIURAT) {
            LOGGER.warn("S'ha bloquejat la modificació d'un albarà de client lliurat. Id: {}", albara.getId());
            throw new IllegalStateException("albara.client.error.modificar.lliurat");
        }
    }

    /**
     * Executa l'operació `obtenirUsuariLoguejat`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `normalitzarTextCerca`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }

    /**
     * Executa l'operació `normalitzarDocument`.
     *
     * @param document paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarDocument(String document) {
        if (document == null) {
            return null;
        }

        return document.trim().toUpperCase().replace(" ", "");
    }
}
