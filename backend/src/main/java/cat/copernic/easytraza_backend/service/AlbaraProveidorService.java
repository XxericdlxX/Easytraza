package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.AlbaraProveidorDto;
import cat.copernic.easytraza_backend.dto.LotProveidorDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.model.LotProveidor;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.model.enums.EstatLot;
import cat.copernic.easytraza_backend.repository.AlbaraProveidorRepository;
import cat.copernic.easytraza_backend.repository.LotProveidorRepository;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<AlbaraProveidor> findAll() {
        return albaraProveidorRepository.findAll();
    }

    public Optional<AlbaraProveidor> findById(Long id) {
        return albaraProveidorRepository.findById(id);
    }

    public AlbaraProveidor save(AlbaraProveidor albaraProveidor) {
        return albaraProveidorRepository.save(albaraProveidor);
    }

    public AlbaraProveidor update(Long id, AlbaraProveidor albaraActualitzat) {
        Optional<AlbaraProveidor> existentOpt = albaraProveidorRepository.findById(id);

        if (existentOpt.isEmpty()) {
            return null;
        }

        AlbaraProveidor existent = existentOpt.get();
        existent.setDataRecepcio(albaraActualitzat.getDataRecepcio());
        existent.setProveidor(albaraActualitzat.getProveidor());

        existent.getLots().clear();
        for (LotProveidor lot : albaraActualitzat.getLots()) {
            lot.setId(null);
            lot.setAlbaraProveidor(existent);
            existent.getLots().add(lot);
        }

        return albaraProveidorRepository.save(existent);
    }

    public void deleteById(Long id) {
        albaraProveidorRepository.deleteById(id);
    }

    public List<AlbaraProveidor> buscar(String proveidorCif, LocalDate dataRecepcio) {
        String cif = normalitzarTextCerca(proveidorCif);

        if (cif.isEmpty() && dataRecepcio == null) {
            return findAll();
        }

        if (!cif.isEmpty() && dataRecepcio != null) {
            return albaraProveidorRepository.findByProveidor_CifContainingIgnoreCaseAndDataRecepcio(cif, dataRecepcio);
        }

        if (!cif.isEmpty()) {
            return albaraProveidorRepository.findByProveidor_CifContainingIgnoreCase(cif);
        }

        return albaraProveidorRepository.findByDataRecepcio(dataRecepcio);
    }

    public String validarAlbara(AlbaraProveidorDto dto, Long idActual) {
        if (dto.getDataRecepcio() == null) {
            return "albara.proveidor.data.obligatoria";
        }

        if (dto.getProveidorCif() == null || dto.getProveidorCif().isBlank()) {
            return "albara.proveidor.proveidor.obligatori";
        }

        if (dto.getLots() == null || dto.getLots().isEmpty()) {
            return "albara.proveidor.lots.obligatori";
        }

        for (LotProveidorDto lotDto : dto.getLots()) {
            if (lotDto.getCodiLot() == null || lotDto.getCodiLot().isBlank()) {
                return "lot.proveidor.codi.obligatori";
            }

            if (lotDto.getQuantitat() == null || lotDto.getQuantitat() <= 0) {
                return "lot.proveidor.quantitat.min";
            }

            if (lotDto.getMateriaPrimaId() == null) {
                return "lot.proveidor.materia.obligatoria";
            }

            Optional<LotProveidor> lotExistent
                    = lotProveidorRepository.findByProveidor_CifAndCodiLotIgnoreCase(
                            dto.getProveidorCif(),
                            lotDto.getCodiLot().trim()
                    );

            if (lotExistent.isPresent()) {
                boolean esMateixLotEnMateixAlbara
                        = idActual != null
                        && lotExistent.get().getAlbaraProveidor() != null
                        && idActual.equals(lotExistent.get().getAlbaraProveidor().getId());

                if (!esMateixLotEnMateixAlbara) {
                    return "lot.proveidor.codi.duplicat";
                }
            }
        }

        return null;
    }

    public AlbaraProveidor convertirDtoAEntity(AlbaraProveidorDto dto) {
        Proveidor proveidor = proveidorRepository.findById(dto.getProveidorCif()).orElse(null);

        AlbaraProveidor albara = new AlbaraProveidor();
        albara.setId(dto.getId());
        albara.setDataRecepcio(dto.getDataRecepcio());
        albara.setProveidor(proveidor);

        List<LotProveidor> lots = new ArrayList<>();

        if (dto.getLots() != null) {
            for (LotProveidorDto lotDto : dto.getLots()) {
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
        }

        albara.setLots(lots);
        return albara;
    }

    public AlbaraProveidorDto convertirEntityADto(AlbaraProveidor entity) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();
        dto.setId(entity.getId());
        dto.setDataRecepcio(entity.getDataRecepcio());
        dto.setProveidorCif(entity.getProveidor().getCif());

        List<LotProveidorDto> lotsDto = new ArrayList<>();
        if (entity.getLots() != null) {
            for (LotProveidor lot : entity.getLots()) {
                LotProveidorDto lotDto = new LotProveidorDto();
                lotDto.setId(lot.getId());
                lotDto.setCodiLot(lot.getCodiLot());
                lotDto.setQuantitat(lot.getQuantitat());
                lotDto.setMateriaPrimaId(lot.getMateriaPrima().getId());
                lotsDto.add(lotDto);
            }
        }

        dto.setLots(lotsDto);
        return dto;
    }

    private String normalitzar(String text) {
        return text == null ? null : text.trim();
    }

    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }
}
