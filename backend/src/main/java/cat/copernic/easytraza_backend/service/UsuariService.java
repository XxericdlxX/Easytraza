package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariService {

    @Autowired
    private UsuariRepository usuariRepository;

    public List<Usuari> findAll() {
        return usuariRepository.findAll();
    }

    public Optional<Usuari> findById(Long id) {
        return usuariRepository.findById(id);
    }

    public Usuari save(Usuari usuari) {
        return usuariRepository.save(usuari);
    }

    public Usuari update(Long id, Usuari usuariActualitzat) {
        Optional<Usuari> usuariExistent = usuariRepository.findById(id);

        if (usuariExistent.isPresent()) {
            Usuari usuari = usuariExistent.get();
            usuari.setNom(usuariActualitzat.getNom());
            usuari.setCognoms(usuariActualitzat.getCognoms());
            usuari.setRol(usuariActualitzat.getRol());
            usuari.setEmail(usuariActualitzat.getEmail());
            usuari.setContrasenya(usuariActualitzat.getContrasenya());

            return usuariRepository.save(usuari);
        } else {
            return null;
        }
    }

    public void deleteById(Long id) {
        usuariRepository.deleteById(id);
    }
}
