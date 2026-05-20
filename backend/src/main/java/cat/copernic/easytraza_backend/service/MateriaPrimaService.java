package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.MateriaPrimaDto;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MateriaPrimaService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.materies");

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    public List<MateriaPrima> findAll() {
        return materiaPrimaRepository.findAll();
    }

    public Optional<MateriaPrima> findById(Long id) {
        return materiaPrimaRepository.findById(id);
    }

    public List<MateriaPrima> buscar(String nom, String descripcio) {
        String nomNormalitzat = normalitzarTextCerca(nom);
        String descripcioNormalitzada = normalitzarTextCerca(descripcio);

        return materiaPrimaRepository.findByNomContainingIgnoreCaseAndDescripcioContainingIgnoreCase(
                nomNormalitzat,
                descripcioNormalitzada
        );
    }

    public MateriaPrima save(MateriaPrima materiaPrima) {
        try {
            MateriaPrima materiaDesada = materiaPrimaRepository.save(materiaPrima);
            LOGGER.info("Matèria primera desada correctament.");
            return materiaDesada;
        } catch (RuntimeException ex) {
            LOGGER.error("Error en desar una matèria primera.", ex);
            throw ex;
        }
    }

    public MateriaPrima update(Long id, MateriaPrima materiaPrimaActualitzada) {
        Optional<MateriaPrima> materiaPrimaExistent = materiaPrimaRepository.findById(id);

        if (materiaPrimaExistent.isPresent()) {
            MateriaPrima materiaPrima = materiaPrimaExistent.get();
            materiaPrima.setNom(materiaPrimaActualitzada.getNom());
            materiaPrima.setDescripcio(materiaPrimaActualitzada.getDescripcio());
            try {
                MateriaPrima materiaDesada = materiaPrimaRepository.save(materiaPrima);
                LOGGER.info("Matèria primera actualitzada correctament.");
                return materiaDesada;
            } catch (RuntimeException ex) {
                LOGGER.error("Error en actualitzar una matèria primera.", ex);
                throw ex;
            }
        } else {
            LOGGER.warn("No s'ha pogut actualitzar la matèria primera perquè no existeix.");
            return null;
        }
    }

    public void deleteById(Long id) {
        try {
            materiaPrimaRepository.deleteById(id);
            LOGGER.info("Matèria primera eliminada correctament.");
        } catch (RuntimeException ex) {
            LOGGER.error("Error en eliminar una matèria primera.", ex);
            throw ex;
        }
    }

    public String validarMateriaPrima(MateriaPrimaDto materiaPrimaDto, Long idActual) {
        Optional<MateriaPrima> materiaAmbMateixNom = materiaPrimaRepository.findByNomIgnoreCase(
                normalitzarText(materiaPrimaDto.getNom())
        );

        if (materiaAmbMateixNom.isPresent()) {
            if (idActual == null || !materiaAmbMateixNom.get().getId().equals(idActual)) {
                LOGGER.warn("Validació de matèria primera rebutjada per nom duplicat.");
                return "materies.error.nom.duplicat";
            }
        }

        return null;
    }

    public MateriaPrima convertirDtoAEntity(MateriaPrimaDto materiaPrimaDto) {
        MateriaPrima materiaPrima = new MateriaPrima();
        materiaPrima.setId(materiaPrimaDto.getId());
        materiaPrima.setNom(normalitzarText(materiaPrimaDto.getNom()));
        materiaPrima.setDescripcio(normalitzarText(materiaPrimaDto.getDescripcio()));
        return materiaPrima;
    }

    public MateriaPrimaDto convertirEntityADto(MateriaPrima materiaPrima) {
        MateriaPrimaDto materiaPrimaDto = new MateriaPrimaDto();
        materiaPrimaDto.setId(materiaPrima.getId());
        materiaPrimaDto.setNom(materiaPrima.getNom());
        materiaPrimaDto.setDescripcio(materiaPrima.getDescripcio());
        return materiaPrimaDto;
    }

    private String normalitzarText(String text) {
        return text == null ? null : text.trim();
    }

    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }
}
