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

@RestController
@RequestMapping("/mobile-api/usuaris")
public class MobileUsuarisApiController {

    @Autowired
    private UsuariService usuariService;

    @GetMapping
    public List<MobileUsuariDto> llistarUsuarisIdentificables() {
        return usuariService.findAll().stream()
                .sorted(Comparator
                        .comparing(this::nomCompletSegur, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Usuari::getEmail, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::toDto)
                .toList();
    }

    private MobileUsuariDto toDto(Usuari usuari) {
        MobileUsuariDto dto = new MobileUsuariDto();
        dto.setId(usuari.getId());
        dto.setNom(usuari.getNom());
        dto.setCognoms(usuari.getCognoms());
        dto.setEmail(usuari.getEmail());
        dto.setRol(usuari.getRol() != null ? usuari.getRol().name() : null);
        return dto;
    }

    private String nomCompletSegur(Usuari usuari) {
        String nom = usuari.getNom() != null ? usuari.getNom().trim() : "";
        String cognoms = usuari.getCognoms() != null ? usuari.getCognoms().trim() : "";
        String complet = (nom + " " + cognoms).trim();

        if (!complet.isBlank()) {
            return complet;
        }

        return usuari.getEmail() != null ? usuari.getEmail() : "";
    }
}
