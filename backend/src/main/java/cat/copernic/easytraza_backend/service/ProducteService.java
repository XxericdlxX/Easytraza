package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.ProducteDto;
import cat.copernic.easytraza_backend.model.Producte;
import cat.copernic.easytraza_backend.repository.ProducteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProducteService {

    @Autowired
    private ProducteRepository producteRepository;

    public List<Producte> findAll() {
        return producteRepository.findAll();
    }

    public Optional<Producte> findById(Long id) {
        return producteRepository.findById(id);
    }

    public Producte save(Producte producte) {
        return producteRepository.save(producte);
    }

    public Producte update(Long id, Producte producteActualitzat) {
        Optional<Producte> producteExistent = producteRepository.findById(id);

        if (producteExistent.isPresent()) {
            Producte producte = producteExistent.get();
            producte.setDescripcio(producteActualitzat.getDescripcio());
            return producteRepository.save(producte);
        } else {
            return null;
        }
    }

    public void deleteById(Long id) {
        producteRepository.deleteById(id);
    }

    public List<Producte> buscarPerDescripcio(String text) {
        if (text == null || text.isBlank()) {
            return findAll();
        }
        return producteRepository.findByDescripcioContainingIgnoreCase(text.trim());
    }

    public String validarProducte(ProducteDto producteDto, Long idActual) {
        String descripcioNormalitzada = normalitzarDescripcio(producteDto.getDescripcio());

        Optional<Producte> producteAmbMateixaDescripcio = producteRepository.findByDescripcio(descripcioNormalitzada);

        if (producteAmbMateixaDescripcio.isPresent()) {
            if (idActual == null || !producteAmbMateixaDescripcio.get().getId().equals(idActual)) {
                return "productes.error.descripcio.duplicada";
            }
        }

        return null;
    }

    public Producte convertirDtoAEntity(ProducteDto producteDto) {
        Producte producte = new Producte();
        producte.setId(producteDto.getId());
        producte.setDescripcio(normalitzarDescripcio(producteDto.getDescripcio()));
        return producte;
    }

    public ProducteDto convertirEntityADto(Producte producte) {
        ProducteDto producteDto = new ProducteDto();
        producteDto.setId(producte.getId());
        producteDto.setDescripcio(producte.getDescripcio());
        return producteDto;
    }

    private String normalitzarDescripcio(String descripcio) {
        return descripcio == null ? null : descripcio.trim();
    }
}
