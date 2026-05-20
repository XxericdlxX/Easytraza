package cat.copernic.easytraza_backend.controller.mobileapi;

import cat.copernic.easytraza_backend.dto.mobile.MobileUsuariDto;
import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.service.UsuariService;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Controlador de l’API mobile `MobileUsuarisApiController` del projecte
 * EasyTraza.
 */
@RestController
@RequestMapping("/mobile-api/usuaris")
public class MobileUsuarisApiController {

    @Autowired
    private UsuariService usuariService;

    /**
     * Executa l'operació `llistarUsuarisIdentificables`.
     *
     * @return resultat obtingut després d'executar l'operació.
     */
    @GetMapping
    public List<MobileUsuariDto> llistarUsuarisIdentificables() {
        return usuariService.findAll().stream()
                .sorted(Comparator
                        .comparing(this::nomCompletSegur, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Usuari::getId, Comparator.nullsLast(Long::compareTo)))
                .map(this::toDto)
                .toList();
    }

    /**
     * Executa l'operació `toDto`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private MobileUsuariDto toDto(Usuari usuari) {
        MobileUsuariDto dto = new MobileUsuariDto();
        dto.setId(usuari.getId());
        dto.setNom(usuari.getNom());
        dto.setCognoms(usuari.getCognoms());
        dto.setRol(usuari.getRol() != null ? usuari.getRol().name() : null);
        dto.setFotoPerfilUrl(construirUrlFotoPerfil(usuari));
        return dto;
    }

    /**
     * Executa l'operació `construirUrlFotoPerfil`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String construirUrlFotoPerfil(Usuari usuari) {
        String fotoPerfil = usuari.getFotoPerfilNom();
        if (fotoPerfil == null || fotoPerfil.isBlank()) {
            return null;
        }

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/web/perfil/foto/{foto}")
                .buildAndExpand(fotoPerfil)
                .toUriString();
    }

    /**
     * Executa l'operació `nomCompletSegur`.
     *
     * @param usuari paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private String nomCompletSegur(Usuari usuari) {
        String nom = usuari.getNom() != null ? usuari.getNom().trim() : "";
        String cognoms = usuari.getCognoms() != null ? usuari.getCognoms().trim() : "";
        String complet = (nom + " " + cognoms).trim();

        if (!complet.isBlank()) {
            return complet;
        }

        return usuari.getId() != null ? "Usuari " + usuari.getId() : "";
    }
}
