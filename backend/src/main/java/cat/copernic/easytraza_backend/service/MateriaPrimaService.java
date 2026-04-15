package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.MateriaPrimaDto;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaPrimaService {

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    public List<MateriaPrima> findAll() {
        return materiaPrimaRepository.findAll();
    }

    public Optional<MateriaPrima> findById(Long id) {
        return materiaPrimaRepository.findById(id);
    }

    public MateriaPrima save(MateriaPrima materiaPrima) {
        return materiaPrimaRepository.save(materiaPrima);
    }

    public MateriaPrima update(Long id, MateriaPrima materiaPrimaActualitzada) {
        Optional<MateriaPrima> materiaPrimaExistent = materiaPrimaRepository.findById(id);

        if (materiaPrimaExistent.isPresent()) {
            MateriaPrima materiaPrima = materiaPrimaExistent.get();
            materiaPrima.setNom(materiaPrimaActualitzada.getNom());
            materiaPrima.setDescripcio(materiaPrimaActualitzada.getDescripcio());
            return materiaPrimaRepository.save(materiaPrima);
        } else {
            return null;
        }
    }

    public void deleteById(Long id) {
        materiaPrimaRepository.deleteById(id);
    }

    public String validarMateriaPrima(MateriaPrimaDto materiaPrimaDto, Long idActual) {
        Optional<MateriaPrima> materiaAmbMateixNom = materiaPrimaRepository.findByNom(materiaPrimaDto.getNom());

        if (materiaAmbMateixNom.isPresent()) {
            if (idActual == null || !materiaAmbMateixNom.get().getId().equals(idActual)) {
                return "Ja existeix una matèria primera amb aquest nom";
            }
        }

        return null;
    }

    public MateriaPrima convertirDtoAEntity(MateriaPrimaDto materiaPrimaDto) {
        MateriaPrima materiaPrima = new MateriaPrima();
        materiaPrima.setId(materiaPrimaDto.getId());
        materiaPrima.setNom(materiaPrimaDto.getNom());
        materiaPrima.setDescripcio(materiaPrimaDto.getDescripcio());
        return materiaPrima;
    }

    public MateriaPrimaDto convertirEntityADto(MateriaPrima materiaPrima) {
        MateriaPrimaDto materiaPrimaDto = new MateriaPrimaDto();
        materiaPrimaDto.setId(materiaPrima.getId());
        materiaPrimaDto.setNom(materiaPrima.getNom());
        materiaPrimaDto.setDescripcio(materiaPrima.getDescripcio());
        return materiaPrimaDto;
    }
}
