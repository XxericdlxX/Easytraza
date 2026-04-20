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

    public void deleteById(Long id) {
        albaraProveidorRepository.deleteById(id);
    }

    public String validarAlbara(AlbaraProveidorDto dto) {
        if (dto.getLots() == null || dto.getLots().isEmpty()) {
            return "albara.proveidor.lots.obligatori";
        }

        for (LotProveidorDto lotDto : dto.getLots()) {
            Optional<LotProveidor> lotExistent = lotProveidorRepository
                    .findByProveidor_CifAndCodiLotIgnoreCase(dto.getProveidorCif(), lotDto.getCodiLot());

            if (lotExistent.isPresent()) {
                return "lot.proveidor.codi.duplicat";
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
                lot.setCodiLot(lotDto.getCodiLot() != null ? lotDto.getCodiLot().trim() : null);
                lot.setQuantitat(lotDto.getQuantitat());
                lot.setEstat(EstatLot.EN_ESTOC);
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
}
