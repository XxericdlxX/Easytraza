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

/**
 * Servei `MateriaPrimaService` del projecte EasyTraza.
 */
@Service
public class MateriaPrimaService {

    private static final Logger LOGGER = LoggerFactory.getLogger("easytraza.materies");

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    /**
     * Executa l'operació `findAll`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<MateriaPrima> findAll() {
        return materiaPrimaRepository.findAll();
    }

    /**
     * Executa l'operació `findById`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public Optional<MateriaPrima> findById(Long id) {
        return materiaPrimaRepository.findById(id);
    }

    /**
     * Executa l'operació `buscar`.
     *
     * @param nom paràmetre necessari per a l'operació.
     * @param descripcio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public List<MateriaPrima> buscar(String nom, String descripcio) {
        String nomNormalitzat = normalitzarTextCerca(nom);
        String descripcioNormalitzada = normalitzarTextCerca(descripcio);

        return materiaPrimaRepository.findByNomContainingIgnoreCaseAndDescripcioContainingIgnoreCase(
                nomNormalitzat,
                descripcioNormalitzada
        );
    }

    /**
     * Executa l'operació `save`.
     *
     * @param materiaPrima paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `update`.
     *
     * @param id paràmetre necessari per a l'operació.
     * @param materiaPrimaActualitzada paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `deleteById`.
     *
     * @param id paràmetre necessari per a l'operació.
     */
    public void deleteById(Long id) {
        try {
            materiaPrimaRepository.deleteById(id);
            LOGGER.info("Matèria primera eliminada correctament.");
        } catch (RuntimeException ex) {
            LOGGER.error("Error en eliminar una matèria primera.", ex);
            throw ex;
        }
    }

    /**
     * Executa l'operació `validarMateriaPrima`.
     *
     * @param materiaPrimaDto paràmetre necessari per a l'operació.
     * @param idActual paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
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

    /**
     * Executa l'operació `convertirDtoAEntity`.
     *
     * @param materiaPrimaDto paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public MateriaPrima convertirDtoAEntity(MateriaPrimaDto materiaPrimaDto) {
        MateriaPrima materiaPrima = new MateriaPrima();
        materiaPrima.setId(materiaPrimaDto.getId());
        materiaPrima.setNom(normalitzarText(materiaPrimaDto.getNom()));
        materiaPrima.setDescripcio(normalitzarText(materiaPrimaDto.getDescripcio()));
        return materiaPrima;
    }

    /**
     * Executa l'operació `convertirEntityADto`.
     *
     * @param materiaPrima paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    public MateriaPrimaDto convertirEntityADto(MateriaPrima materiaPrima) {
        MateriaPrimaDto materiaPrimaDto = new MateriaPrimaDto();
        materiaPrimaDto.setId(materiaPrima.getId());
        materiaPrimaDto.setNom(materiaPrima.getNom());
        materiaPrimaDto.setDescripcio(materiaPrima.getDescripcio());
        return materiaPrimaDto;
    }

    /**
     * Executa l'operació `normalitzarText`.
     *
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String normalitzarText(String text) {
        return text == null ? null : text.trim();
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
}
