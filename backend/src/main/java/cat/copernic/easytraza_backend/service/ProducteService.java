package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.ProducteDto;
import cat.copernic.easytraza_backend.model.LiniaClient;
import cat.copernic.easytraza_backend.model.Producte;
import cat.copernic.easytraza_backend.repository.AlbaraClientRepository;
import cat.copernic.easytraza_backend.repository.ProducteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProducteService {

    @Autowired
    private ProducteRepository producteRepository;

    @Autowired
    private AlbaraClientRepository albaraClientRepository;

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
            producte.setNom(producteActualitzat.getNom());
            producte.setDescripcio(producteActualitzat.getDescripcio());
            return producteRepository.save(producte);
        }

        return null;
    }

    public void deleteById(Long id) {
        producteRepository.deleteById(id);
    }

    public List<Producte> buscar(String nom, String descripcio) {
        String nomNormalitzat = normalitzarTextCerca(nom);
        String descripcioNormalitzada = normalitzarTextCerca(descripcio);

        if (nomNormalitzat.isEmpty() && descripcioNormalitzada.isEmpty()) {
            return findAll();
        }

        return producteRepository.findByNomContainingIgnoreCaseAndDescripcioContainingIgnoreCase(
                nomNormalitzat,
                descripcioNormalitzada
        );
    }

    public List<LiniaClient> cercarProduccioLotsPerProducte(Long producteId) {
        if (producteId == null) {
            return List.of();
        }

        return albaraClientRepository.findLiniesProduccioByProducteId(producteId);
    }

    public String validarProducte(ProducteDto producteDto, Long idActual) {
        String nomNormalitzat = normalitzar(producteDto.getNom());

        Optional<Producte> producteAmbMateixNom = producteRepository.findByNomIgnoreCase(nomNormalitzat);

        if (producteAmbMateixNom.isPresent()) {
            if (idActual == null || !producteAmbMateixNom.get().getId().equals(idActual)) {
                return "productes.error.nom.duplicat";
            }
        }

        return null;
    }

    public Producte convertirDtoAEntity(ProducteDto producteDto) {
        Producte producte = new Producte();
        producte.setId(producteDto.getId());
        producte.setNom(normalitzar(producteDto.getNom()));
        producte.setDescripcio(normalitzar(producteDto.getDescripcio()));
        return producte;
    }

    public ProducteDto convertirEntityADto(Producte producte) {
        ProducteDto producteDto = new ProducteDto();
        producteDto.setId(producte.getId());
        producteDto.setNom(producte.getNom());
        producteDto.setDescripcio(producte.getDescripcio());
        return producteDto;
    }

    private String normalitzar(String text) {
        return text == null ? null : text.trim();
    }

    private String normalitzarTextCerca(String text) {
        return text == null ? "" : text.trim();
    }
}
