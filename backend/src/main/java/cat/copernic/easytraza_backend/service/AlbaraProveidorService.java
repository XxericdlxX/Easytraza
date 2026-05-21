package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.AlbaraProveidorDto;
import cat.copernic.easytraza_backend.dto.LotProveidorDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import cat.copernic.easytraza_backend.repository.AlbaraProveidorRepository;
import cat.copernic.easytraza_backend.repository.LotProveidorRepository;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei `AlbaraProveidorService` del projecte EasyTraza.
 */
@Service
public class AlbaraProveidorService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.albarans.proveidor");

    @Autowired
    private AlbaraProveidorRepository albaraProveidorRepository;

    @Autowired
    private LotProveidorRepository lotProveidorRepository;

    @Autowired
    private ProveidorRepository proveidorRepository;

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<AlbaraProveidor> findAll() {
        return albaraProveidorRepository.findAll();
    }

    /**
     * Executa l'operació `findById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Optional<AlbaraProveidor> findById(Long id) {
        return albaraProveidorRepository.findById(id);
    }

    /**
     * Executa l'operació `buscar`.
     *
     * @param proveidorCif paràmetre necessari per a l'operació.
     * @param dataRecepcio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<AlbaraProveidor> buscar(String proveidorCif, LocalDate dataRecepcio) {
        String cifNormalitzat = normalitzarTextCerca(proveidorCif);

        if (cifNormalitzat.isEmpty() && dataRecepcio == null) {
            return findAll();
        }

        if (!cifNormalitzat.isEmpty() && dataRecepcio != null) {
            return albaraProveidorRepository.findByProveidor_CifContainingIgnoreCaseAndDataRecepcio(
                    cifNormalitzat,
                    dataRecepcio
            );
        }

        if (!cifNormalitzat.isEmpty()) {
            return albaraProveidorRepository.findByProveidor_CifContainingIgnoreCase(cifNormalitzat);
        }

        return albaraProveidorRepository.findByDataRecepcio(dataRecepcio);
    }

    /**
     * Executa l'operació `save`.
     *
     * @param albaraProveidor paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public AlbaraProveidor save(AlbaraProveidor albaraProveidor) {
        try {
            prepararLots(albaraProveidor);
            AlbaraProveidor albaraDesat = albaraProveidorRepository.save(albaraProveidor);
            LOGGER.info("Albarà de proveïdor desat correctament amb id {}.", albaraDesat.getId());
            return albaraDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en desar un albarà de proveïdor.", ex);
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
    public AlbaraProveidor update(Long id, AlbaraProveidor albaraActualitzat) {
        Optional<AlbaraProveidor> existentOpt = albaraProveidorRepository.findById(id);

        if (existentOpt.isEmpty()) {
            LOGGER.warn("No s'ha pogut actualitzar l'albarà de proveïdor perquè no existeix. Id: {}", id);
            return null;
        }

        AlbaraProveidor existent = existentOpt.get();
        existent.setDataRecepcio(albaraActualitzat.getDataRecepcio());
        existent.setProveidor(albaraActualitzat.getProveidor());
        existent.setDocumentOcrNomOriginal(albaraActualitzat.getDocumentOcrNomOriginal());
        existent.setDocumentOcrNomGuardat(albaraActualitzat.getDocumentOcrNomGuardat());
        existent.setDocumentOcrContentType(albaraActualitzat.getDocumentOcrContentType());
        existent.setDocumentOcrRuta(albaraActualitzat.getDocumentOcrRuta());

        if (albaraActualitzat.getUsuariReceptor() != null) {
            existent.setUsuariReceptor(albaraActualitzat.getUsuariReceptor());
        }

        existent.getLots().clear();
        albaraProveidorRepository.flush();

        for (LotProveidor lot : albaraActualitzat.getLots()) {
            lot.setId(null);
            lot.setAlbaraProveidor(existent);
            existent.getLots().add(lot);
        }

        try {
            prepararLots(existent);
            AlbaraProveidor albaraDesat = albaraProveidorRepository.saveAndFlush(existent);
            LOGGER.info("Albarà de proveïdor actualitzat correctament amb id {}.", albaraDesat.getId());
            return albaraDesat;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en actualitzar l'albarà de proveïdor amb id {}.", id, ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `deleteById`.
     *
     * @param id paràmetre necessari per a l'operació.
     */
    @Transactional
    public void deleteById(Long id) {
        AlbaraProveidor albara = albaraProveidorRepository.findById(id).orElse(null);

        if (albara == null) {
            LOGGER.warn("No s'ha pogut eliminar l'albarà de proveïdor perquè no existeix. Id: {}", id);
            throw new IllegalArgumentException("albara.proveidor.error.no.trobat");
        }

        if (albara.getLots() != null) {
            boolean teLotsActius = albara.getLots().stream()
                    .anyMatch(lot -> lot.getEstat() != null
                    && ("OBERT".equals(lot.getEstat().name())
                    || "INICIAT".equals(lot.getEstat().name())));

            if (teLotsActius) {
                LOGGER.warn("S'ha bloquejat l'eliminació de l'albarà de proveïdor perquè té lots actius. Id: {}", id);
                throw new IllegalStateException("albara.proveidor.error.eliminar.lots.iniciats");
            }
        }

        try {
            albaraProveidorRepository.delete(albara);
            LOGGER.info("Albarà de proveïdor eliminat correctament amb id {}.", id);
        } catch (RuntimeException ex) {
            LOGGER.error("Error en eliminar l'albarà de proveïdor amb id {}.", id, ex);
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
    public String validarAlbara(AlbaraProveidorDto dto, Long idActual) {
        if (dto.getDataRecepcio() == null) {
            LOGGER.warn("Validació d'albarà de proveïdor rebutjada perquè falta la data de recepció.");
            return "albara.proveidor.data.obligatoria";
        }

        completarReferenciesOcr(dto);

        Proveidor proveidor = obtenirOCrearProveidor(dto);
        if (proveidor == null) {
            LOGGER.warn("Validació d'albarà de proveïdor rebutjada perquè el proveïdor no existeix.");
            return "albara.proveidor.error.proveidor.no.trobat";
        }

        List<LotProveidorDto> lotsValids = obtenirLotsValids(dto);
        if (lotsValids.isEmpty()) {
            LOGGER.warn("Validació d'albarà de proveïdor rebutjada perquè no conté lots vàlids.");
            return "albara.proveidor.lots.obligatori";
        }

        Set<String> lotsRevisats = new HashSet<>();

        for (LotProveidorDto lotDto : lotsValids) {
            String errorLot = validarLot(lotDto, proveidor.getCif(), dto.getDataRecepcio(), idActual, lotsRevisats);
            if (errorLot != null) {
                LOGGER.warn("Validació d'albarà de proveïdor rebutjada per un error de lot: {}", errorLot);
                return errorLot;
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
    public AlbaraProveidor convertirDtoAEntity(AlbaraProveidorDto dto) {
        completarReferenciesOcr(dto);

        Proveidor proveidor = obtenirOCrearProveidor(dto);
        Usuari usuariLoguejat = obtenirUsuariLoguejat();

        AlbaraProveidor albara = new AlbaraProveidor();
        albara.setId(dto.getId());
        albara.setDataRecepcio(dto.getDataRecepcio());
        albara.setProveidor(proveidor);
        albara.setUsuariReceptor(usuariLoguejat);
        albara.setDocumentOcrNomOriginal(dto.getDocumentOcrNomOriginal());
        albara.setDocumentOcrNomGuardat(dto.getDocumentOcrNomGuardat());
        albara.setDocumentOcrContentType(dto.getDocumentOcrContentType());
        albara.setDocumentOcrRuta(dto.getDocumentOcrRuta());

        List<LotProveidor> lots = new ArrayList<>();

        for (LotProveidorDto lotDto : obtenirLotsValids(dto)) {
            MateriaPrima materiaPrima = obtenirOCrearMateriaPrima(lotDto);

            LotProveidor lot = new LotProveidor();
            lot.setId(lotDto.getId());
            lot.setCodiLot(normalitzar(lotDto.getCodiLot()));
            lot.setCodiMateriaPrimaOcr(normalitzarNullable(lotDto.getCodiMateriaPrimaOcr()));
            lot.setQuantitat(lotDto.getQuantitat());
            lot.setEstat(EstatLot.EN_ESTOC);
            lot.setDataObertura(null);
            lot.setDataAcabament(null);
            lot.setMateriaPrima(materiaPrima);
            lot.setProveidor(proveidor);
            lot.setAlbaraProveidor(albara);

            lots.add(lot);
        }

        albara.setLots(lots);
        return albara;
    }

    /**
     * Executa l'operació `convertirEntityADto`.
     *
     * @param entity paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public AlbaraProveidorDto convertirEntityADto(AlbaraProveidor entity) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();
        dto.setId(entity.getId());
        dto.setDataRecepcio(entity.getDataRecepcio());
        dto.setDocumentOcrNomOriginal(entity.getDocumentOcrNomOriginal());
        dto.setDocumentOcrNomGuardat(entity.getDocumentOcrNomGuardat());
        dto.setDocumentOcrContentType(entity.getDocumentOcrContentType());
        dto.setDocumentOcrRuta(entity.getDocumentOcrRuta());

        if (entity.getProveidor() != null) {
            dto.setProveidorCif(entity.getProveidor().getCif());
            dto.setProveidorNomDetectat(entity.getProveidor().getNom());
        }

        if (entity.getUsuariReceptor() != null) {
            String nom = entity.getUsuariReceptor().getNom() != null ? entity.getUsuariReceptor().getNom() : "";
            String cognoms = entity.getUsuariReceptor().getCognoms() != null ? entity.getUsuariReceptor().getCognoms() : "";
            dto.setUsuariReceptorNom((nom + " " + cognoms).trim());
        }

        List<LotProveidorDto> lotsDto = new ArrayList<>();

        if (entity.getLots() != null) {
            for (LotProveidor lot : entity.getLots()) {
                LotProveidorDto lotDto = new LotProveidorDto();
                lotDto.setId(lot.getId());
                lotDto.setCodiLot(lot.getCodiLot());
                lotDto.setCodiMateriaPrimaOcr(lot.getCodiMateriaPrimaOcr());
                lotDto.setQuantitat(lot.getQuantitat());

                if (lot.getMateriaPrima() != null) {
                    lotDto.setMateriaPrimaId(lot.getMateriaPrima().getId());
                    lotDto.setMateriaPrimaNomDetectada(lot.getMateriaPrima().getNom());
                }

                lotsDto.add(lotDto);
            }
        }

        if (lotsDto.isEmpty()) {
            lotsDto.add(new LotProveidorDto());
        }

        dto.setLots(lotsDto);
        return dto;
    }

    /**
     * Executa l'operació `assegurarMinimUnLot`.
     *
     * @param dto paràmetre necessari per a l'operació.
     */
    public void assegurarMinimUnLot(AlbaraProveidorDto dto) {
        if (dto.getLots() == null) {
            dto.setLots(new ArrayList<>());
        }

        if (dto.getLots().isEmpty()) {
            dto.getLots().add(new LotProveidorDto());
        }
    }

    /**
     * Executa l'operació `completarReferenciesOcr`.
     *
     * @param dto paràmetre necessari per a l'operació.
     */
    public void completarReferenciesOcr(AlbaraProveidorDto dto) {
        if (dto == null) {
            return;
        }

        String cif = normalitzarDocument(dto.getProveidorCif());

        if (cif != null && !cif.isBlank()) {
            dto.setProveidorCif(cif);
        }

        if (dto.getLots() == null) {
            return;
        }

        for (LotProveidorDto lotDto : dto.getLots()) {
            completarMateriaPrimaSiExisteix(lotDto);
        }
    }

    /**
     * Executa l'operació `completarMateriaPrimaSiExisteix`.
     *
     * @param lotDto paràmetre necessari per a l'operació.
     */
    private void completarMateriaPrimaSiExisteix(LotProveidorDto lotDto) {
        if (lotDto == null || lotDto.getMateriaPrimaId() != null) {
            return;
        }

        String nomDetectat = normalitzar(lotDto.getMateriaPrimaNomDetectada());

        if (nomDetectat == null || nomDetectat.isBlank()) {
            return;
        }

        Optional<MateriaPrima> existent = materiaPrimaRepository.findByNomIgnoreCase(nomDetectat);

        if (existent.isPresent()) {
            lotDto.setMateriaPrimaId(existent.get().getId());
        }
    }

    /**
     * Executa l'operació `validarLot`.
     *
     * @param lotDto paràmetre necessari per a l'operació.
     * @param proveidorCif paràmetre necessari per a l'operació.
     * @param dataRecepcio paràmetre necessari per a l'operació.
     * @param idActual paràmetre necessari per a l'operació.
     * @param lotsRevisats paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String validarLot(LotProveidorDto lotDto, String proveidorCif, LocalDate dataRecepcio, Long idActual, Set<String> lotsRevisats) {
        String codiLot = normalitzar(lotDto.getCodiLot());

        if (codiLot == null || codiLot.isBlank()) {
            return "lot.proveidor.codi.obligatori";
        }

        if (lotDto.getQuantitat() == null) {
            return "lot.proveidor.quantitat.obligatoria";
        }

        if (lotDto.getQuantitat() <= 0) {
            return "lot.proveidor.quantitat.min";
        }

        MateriaPrima materiaPrima = obtenirOCrearMateriaPrima(lotDto);
        if (materiaPrima == null) {
            return "albara.proveidor.error.materia.no.trobada";
        }

        String clauRecepcio = proveidorCif.trim().toUpperCase() + "|" + dataRecepcio + "|" + codiLot.toUpperCase();
        if (!lotsRevisats.add(clauRecepcio)) {
            return "lot.proveidor.codi.duplicat.recepcio";
        }

        long repetits = lotProveidorRepository.countLotRepetitEnRecepcio(
                proveidorCif,
                dataRecepcio,
                codiLot,
                idActual
        );

        return repetits > 0 ? "lot.proveidor.codi.duplicat.recepcio" : null;
    }

    /**
     * Executa l'operació `obtenirOCrearProveidor`.
     *
     * @param dto paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private Proveidor obtenirOCrearProveidor(AlbaraProveidorDto dto) {
        String cif = normalitzarDocument(dto.getProveidorCif());

        if (cif != null && !cif.isBlank()) {
            Optional<Proveidor> existent = proveidorRepository.findById(cif);
            if (existent.isPresent()) {
                return existent.get();
            }
        }

        if (!dto.isCrearProveidorSiNoExisteix()) {
            return null;
        }

        Proveidor proveidor = new Proveidor();
        proveidor.setCif(cif != null && !cif.isBlank() ? cif : generarCifTemporal(dto.getProveidorNomDetectat()));
        proveidor.setNom(valorOTextPerDefecte(dto.getProveidorNomDetectat(), "Proveïdor OCR"));
        proveidor.setAdreca("Pendent de revisar");
        proveidor.setNotes("Creat automàticament des de la revisió OCR");
        proveidor.setTelefon(null);
        proveidor.setEmail(null);

        return proveidorRepository.save(proveidor);
    }

    /**
     * Executa l'operació `obtenirOCrearMateriaPrima`.
     *
     * @param lotDto paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private MateriaPrima obtenirOCrearMateriaPrima(LotProveidorDto lotDto) {
        if (lotDto.getMateriaPrimaId() != null) {
            return materiaPrimaRepository.findById(lotDto.getMateriaPrimaId()).orElse(null);
        }

        if (!lotDto.isCrearMateriaPrimaSiNoExisteix()) {
            return null;
        }

        String nomDetectat = normalitzar(lotDto.getMateriaPrimaNomDetectada());
        if (nomDetectat == null || nomDetectat.isBlank()) {
            return null;
        }

        Optional<MateriaPrima> existent = materiaPrimaRepository.findByNomIgnoreCase(nomDetectat);
        if (existent.isPresent()) {
            lotDto.setMateriaPrimaId(existent.get().getId());
            return existent.get();
        }

        MateriaPrima materiaPrima = new MateriaPrima();
        materiaPrima.setNom(nomDetectat);
        materiaPrima.setDescripcio("Creada automàticament des de la revisió OCR");

        MateriaPrima creada = materiaPrimaRepository.save(materiaPrima);
        lotDto.setMateriaPrimaId(creada.getId());

        return creada;
    }

    /**
     * Executa l'operació `obtenirLotsValids`.
     *
     * @param dto paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private List<LotProveidorDto> obtenirLotsValids(AlbaraProveidorDto dto) {
        List<LotProveidorDto> valids = new ArrayList<>();

        if (dto.getLots() == null) {
            return valids;
        }

        for (LotProveidorDto lot : dto.getLots()) {
            boolean teCodi = lot.getCodiLot() != null && !lot.getCodiLot().isBlank();
            boolean teQuantitat = lot.getQuantitat() != null;
            boolean teMateria = lot.getMateriaPrimaId() != null
                    || (lot.isCrearMateriaPrimaSiNoExisteix()
                    && lot.getMateriaPrimaNomDetectada() != null
                    && !lot.getMateriaPrimaNomDetectada().isBlank());

            if (teCodi || teQuantitat || teMateria) {
                valids.add(lot);
            }
        }

        return valids;
    }

    /**
     * Executa l'operació `prepararLots`.
     *
     * @param albara paràmetre necessari per a l'operació.
     */
    private void prepararLots(AlbaraProveidor albara) {
        if (albara.getLots() == null) {
            return;
        }

        for (LotProveidor lot : albara.getLots()) {
            lot.setAlbaraProveidor(albara);

            if (lot.getProveidor() == null) {
                lot.setProveidor(albara.getProveidor());
            }

            if (lot.getEstat() == null) {
                lot.setEstat(EstatLot.EN_ESTOC);
            }
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
            return usuariRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        }

        return null;
    }

    /**
     * Executa l'operació `generarCifTemporal`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String generarCifTemporal(String nom) {
        String base = valorOTextPerDefecte(nom, "PROVEIDOR")
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "");

        if (base.length() > 8) {
            base = base.substring(0, 8);
        }

        String cif = "OCR" + base;

        int contador = 1;
        String candidat = cif;

        while (proveidorRepository.findById(candidat).isPresent()) {
            candidat = cif + contador;
            contador++;
        }

        return candidat;
    }

    /**
     * Executa l'operació `valorOTextPerDefecte`.
     *
     * @param valor paràmetre necessari per a l'operació.
     * @param defecte paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String valorOTextPerDefecte(String valor, String defecte) {
        return valor == null || valor.isBlank() ? defecte : valor.trim();
    }

    /**
     * Executa l'operació `normalitzar`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzar(String text) {
        return text == null ? null : text.trim();
    }

    /**
     * Executa l'operació `normalitzarNullable`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarNullable(String text) {
        String normalitzat = normalitzar(text);
        return normalitzat == null || normalitzat.isBlank() ? null : normalitzat;
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
        return document == null ? null : document.trim().toUpperCase().replace(" ", "");
    }
}
