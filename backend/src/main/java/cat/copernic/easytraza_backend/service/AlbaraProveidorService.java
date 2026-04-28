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
        prepararLotsAbansDeGuardar(albaraProveidor);
        return albaraProveidorRepository.save(albaraProveidor);
    }

    @Transactional
    public AlbaraProveidor update(Long id, AlbaraProveidor albaraActualitzat) {
        Optional<AlbaraProveidor> existentOpt = albaraProveidorRepository.findById(id);

        if (existentOpt.isEmpty()) {
            return null;
        }

        AlbaraProveidor existent = existentOpt.get();
        validarAlbaraEditable(existent);

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

        prepararLotsAbansDeGuardar(existent);
        return albaraProveidorRepository.saveAndFlush(existent);
    }

    public void deleteById(Long id) {
        Optional<AlbaraProveidor> albaraOpt = albaraProveidorRepository.findById(id);

        if (albaraOpt.isPresent()) {
            validarAlbaraEditable(albaraOpt.get());
        }

        albaraProveidorRepository.deleteById(id);
    }

    public String validarAlbara(AlbaraProveidorDto dto, Long idActual) {
        if (dto.getDataRecepcio() == null) {
            return "albara.proveidor.data.obligatoria";
        }

        if (dto.getProveidorCif() == null || dto.getProveidorCif().isBlank()) {
            return "albara.proveidor.proveidor.obligatori";
        }

        String proveidorCifNormalitzat = normalitzarDocument(dto.getProveidorCif());

        Optional<Proveidor> proveidor = proveidorRepository.findById(proveidorCifNormalitzat);
        if (proveidor.isEmpty()) {
            return "albara.proveidor.error.proveidor.no.trobat";
        }

        if (idActual != null) {
            Optional<AlbaraProveidor> albaraExistent = albaraProveidorRepository.findById(idActual);

            if (albaraExistent.isPresent() && !esAlbaraEditable(albaraExistent.get())) {
                return "albara.proveidor.error.modificar.lots.no.estoc";
            }
        }

        if (dto.getLots() == null || dto.getLots().isEmpty()) {
            return "albara.proveidor.lots.obligatori";
        }

        boolean hiHaAlgunLotAmbDades = false;

        for (LotProveidorDto lotDto : dto.getLots()) {
            boolean teCodi = lotDto.getCodiLot() != null && !lotDto.getCodiLot().isBlank();
            boolean teQuantitat = lotDto.getQuantitat() != null;
            boolean teMateria = lotDto.getMateriaPrimaId() != null;

            if (!teCodi && !teQuantitat && !teMateria) {
                continue;
            }

            hiHaAlgunLotAmbDades = true;

            if (!teCodi) {
                return "lot.proveidor.codi.obligatori";
            }

            if (lotDto.getCodiLot().trim().length() > 100) {
                return "lot.proveidor.codi.max";
            }

            if (!teQuantitat) {
                return "lot.proveidor.quantitat.obligatoria";
            }

            if (lotDto.getQuantitat() <= 0) {
                return "lot.proveidor.quantitat.min";
            }

            if (!teMateria) {
                return "lot.proveidor.materia.obligatoria";
            }

            Optional<MateriaPrima> materiaPrima = materiaPrimaRepository.findById(lotDto.getMateriaPrimaId());
            if (materiaPrima.isEmpty()) {
                return "albara.proveidor.error.materia.no.trobada";
            }

            String codiLotNormalitzat = normalitzar(lotDto.getCodiLot());

            Optional<LotProveidor> lotExistent
                    = lotProveidorRepository.findByProveidor_CifAndCodiLotIgnoreCase(
                            proveidorCifNormalitzat,
                            codiLotNormalitzat
                    );

            if (lotExistent.isPresent()) {
                boolean esMateixLotMateixAlbara
                        = idActual != null
                        && lotExistent.get().getAlbaraProveidor() != null
                        && idActual.equals(lotExistent.get().getAlbaraProveidor().getId());

                if (!esMateixLotMateixAlbara) {
                    return "lot.proveidor.codi.duplicat";
                }
            }
        }

        if (!hiHaAlgunLotAmbDades) {
            return "albara.proveidor.lots.obligatori";
        }

        return null;
    }

    public AlbaraProveidor convertirDtoAEntity(AlbaraProveidorDto dto) {
        String proveidorCifNormalitzat = normalitzarDocument(dto.getProveidorCif());

        Proveidor proveidor = proveidorRepository.findById(proveidorCifNormalitzat).orElse(null);
        Usuari usuariLoguejat = obtenirUsuariLoguejat();

        AlbaraProveidor albara = new AlbaraProveidor();
        albara.setId(dto.getId());
        albara.setDataRecepcio(dto.getDataRecepcio());
        albara.setProveidor(proveidor);
        albara.setUsuariReceptor(usuariLoguejat);

        List<LotProveidor> lots = new ArrayList<>();

        for (LotProveidorDto lotDto : obtenirLotsValids(dto.getLots())) {
            MateriaPrima materiaPrima = materiaPrimaRepository.findById(lotDto.getMateriaPrimaId()).orElse(null);

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

    public boolean esAlbaraEditable(AlbaraProveidor albara) {
        if (albara == null || albara.getLots() == null) {
            return true;
        }

        for (LotProveidor lot : albara.getLots()) {
            if (lot.getEstat() != EstatLot.EN_ESTOC) {
                return false;
            }
        }

        return true;
    }

    private void validarAlbaraEditable(AlbaraProveidor albara) {
        if (!esAlbaraEditable(albara)) {
            throw new IllegalStateException("albara.proveidor.error.modificar.lots.no.estoc");
        }
    }

    private void prepararLotsAbansDeGuardar(AlbaraProveidor albara) {
        if (albara == null || albara.getLots() == null) {
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

            if (lot.getEstat() == EstatLot.EN_ESTOC) {
                lot.setDataObertura(null);
                lot.setDataAcabament(null);
            }
        }
    }

    private List<LotProveidorDto> obtenirLotsValids(List<LotProveidorDto> lots) {
        List<LotProveidorDto> valids = new ArrayList<>();
        if (lots == null) {
            return valids;
        }

        for (LotProveidorDto lot : lots) {
            boolean teCodi = lot.getCodiLot() != null && !lot.getCodiLot().isBlank();
            boolean teQuantitat = lot.getQuantitat() != null;
            boolean teMateria = lot.getMateriaPrimaId() != null;

            if (teCodi && teQuantitat && teMateria && lot.getQuantitat() > 0) {
                valids.add(lot);
            }
        }

        return valids;
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

    private String normalitzar(String text) {
        return text == null ? null : text.trim();
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
}
