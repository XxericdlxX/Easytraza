package cat.copernic.easytraza_backend.service;

import cat.copernic.easytraza_backend.dto.UsuariDto;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.Rol;
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

            // L'email és la clau funcional i no s'ha de modificar
            usuari.setEmail(usuari.getEmail());

            // Si a edició es deixa la contrasenya buida, es manté l'actual
            if (usuariActualitzat.getContrasenya() != null && !usuariActualitzat.getContrasenya().isBlank()) {
                usuari.setContrasenya(usuariActualitzat.getContrasenya());
            }

            return usuariRepository.save(usuari);
        } else {
            return null;
        }
    }

    public void deleteById(Long id) {
        usuariRepository.deleteById(id);
    }

    public String validarUsuari(UsuariDto usuariDto, Long idActual) {
        Optional<Usuari> usuariAmbMateixEmail = usuariRepository.findByEmail(usuariDto.getEmail());

        if (usuariAmbMateixEmail.isPresent()) {
            if (idActual == null || !usuariAmbMateixEmail.get().getId().equals(idActual)) {
                return "usuaris.error.email.duplicat";
            }
        }

        if (idActual == null && (usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank())) {
            return "usuaris.contrasenya.obligatoria";
        }

        if (usuariDto.getRol() == Rol.ADMIN) {
            if (idActual == null) {
                if (usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank()) {
                    return "usuaris.admin.contrasenya.obligatoria";
                }
            } else {
                Optional<Usuari> usuariExistent = usuariRepository.findById(idActual);
                if (usuariExistent.isPresent()) {
                    String contrasenyaActual = usuariExistent.get().getContrasenya();
                    boolean novaContrasenyaBuida = usuariDto.getContrasenya() == null || usuariDto.getContrasenya().isBlank();
                    boolean contrasenyaActualBuida = contrasenyaActual == null || contrasenyaActual.isBlank();

                    if (novaContrasenyaBuida && contrasenyaActualBuida) {
                        return "usuaris.admin.contrasenya.obligatoria";
                    }
                }
            }
        }

        return null;
    }

    public Usuari convertirDtoAEntity(UsuariDto usuariDto) {
        Usuari usuari = new Usuari();
        usuari.setId(usuariDto.getId());
        usuari.setNom(usuariDto.getNom());
        usuari.setCognoms(usuariDto.getCognoms());
        usuari.setRol(usuariDto.getRol());
        usuari.setEmail(usuariDto.getEmail());
        usuari.setContrasenya(usuariDto.getContrasenya());
        return usuari;
    }

    public UsuariDto convertirEntityADto(Usuari usuari) {
        UsuariDto usuariDto = new UsuariDto();
        usuariDto.setId(usuari.getId());
        usuariDto.setNom(usuari.getNom());
        usuariDto.setCognoms(usuari.getCognoms());
        usuariDto.setRol(usuari.getRol());
        usuariDto.setEmail(usuari.getEmail());
        usuariDto.setContrasenya("");
        return usuariDto;
    }
}
