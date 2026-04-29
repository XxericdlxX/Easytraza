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
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlbaraProveidorService {

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

    public List<AlbaraProveidor> findAll() {
        return albaraProveidorRepository.findAll();
    }

    public Optional<AlbaraProveidor> findById(Long id) {
        return albaraProveidorRepository.findById(id);
    }

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

    public AlbaraProveidor save(AlbaraProveidor albaraProveidor) {
        prepararLots(albaraProveidor);
        return albaraProveidorRepository.save(albaraProveidor);
    }

    @Transactional
    public AlbaraProveidor update(Long id, AlbaraProveidor albaraActualitzat) {
        Optional<AlbaraProveidor> existentOpt = albaraProveidorRepository.findById(id);

        if (existentOpt.isEmpty()) {
            return null;
        }

        AlbaraProveidor existent = existentOpt.get();
        existent.setDataRecepcio(albaraActualitzat.getDataRecepcio());
        existent.setProveidor(albaraActualitzat.getProveidor());

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

        prepararLots(existent);
        return albaraProveidorRepository.saveAndFlush(existent);
    }

    public void deleteById(Long id) {
        albaraProveidorRepository.deleteById(id);
    }

    public String validarAlbara(AlbaraProveidorDto dto, Long idActual) {
        if (dto.getDataRecepcio() == null) {
            return "albara.proveidor.data.obligatoria";
        }

        Proveidor proveidor = obtenirOCrearProveidor(dto);
        if (proveidor == null) {
            return "albara.proveidor.error.proveidor.no.trobat";
        }

        List<LotProveidorDto> lotsValids = obtenirLotsValids(dto);
        if (lotsValids.isEmpty()) {
            return "albara.proveidor.lots.obligatori";
        }

        Set<String> lotsRevisats = new HashSet<>();

        for (LotProveidorDto lotDto : lotsValids) {
            String errorLot = validarLot(lotDto, proveidor.getCif(), dto.getDataRecepcio(), idActual, lotsRevisats);
            if (errorLot != null) {
                return errorLot;
            }
        }

        return null;
    }

    public AlbaraProveidor convertirDtoAEntity(AlbaraProveidorDto dto) {
        Proveidor proveidor = obtenirOCrearProveidor(dto);
        Usuari usuariLoguejat = obtenirUsuariLoguejat();

        AlbaraProveidor albara = new AlbaraProveidor();
        albara.setId(dto.getId());
        albara.setDataRecepcio(dto.getDataRecepcio());
        albara.setProveidor(proveidor);
        albara.setUsuariReceptor(usuariLoguejat);

        List<LotProveidor> lots = new ArrayList<>();

        for (LotProveidorDto lotDto : obtenirLotsValids(dto)) {
            MateriaPrima materiaPrima = obtenirOCrearMateriaPrima(lotDto);

            LotProveidor lot = new LotProveidor();
            lot.setId(lotDto.getId());
            lot.setCodiLot(normalitzar(lotDto.getCodiLot()));
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

    public AlbaraProveidorDto convertirEntityADto(AlbaraProveidor entity) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();
        dto.setId(entity.getId());
        dto.setDataRecepcio(entity.getDataRecepcio());

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

    public void assegurarMinimUnLot(AlbaraProveidorDto dto) {
        if (dto.getLots() == null) {
            dto.setLots(new ArrayList<>());
        }

        if (dto.getLots().isEmpty()) {
            dto.getLots().add(new LotProveidorDto());
        }
    }

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

    private Usuari obtenirUsuariLoguejat() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return usuariRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        }

        return null;
    }

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

    private String valorOTextPerDefecte(String valor, String defecte) {
        return valor == null || valor.isBlank() ? defecte : valor.trim();
    }

    private String normalitzar(String text) {
        return text == null ? null : text.trim();
    }

    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }

    private String normalitzarDocument(String document) {
        return document == null ? null : document.trim().toUpperCase().replace(" ", "");
    }
}
