package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.service.UsuariService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {

    @Autowired
    private UsuariService usuariService;

    @GetMapping
    public List<Usuari> getAllUsuaris() {
        return usuariService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuari> getUsuariById(@PathVariable Long id) {
        Optional<Usuari> usuari = usuariService.findById(id);

        if (usuari.isPresent()) {
            return ResponseEntity.ok(usuari.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Usuari> createUsuari(@RequestBody Usuari usuari) {
        Usuari nouUsuari = usuariService.save(usuari);
        return ResponseEntity.ok(nouUsuari);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuari> updateUsuari(@PathVariable Long id, @RequestBody Usuari usuariActualitzat) {
        Usuari usuari = usuariService.update(id, usuariActualitzat);

        if (usuari != null) {
            return ResponseEntity.ok(usuari);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuari(@PathVariable Long id) {
        Optional<Usuari> usuari = usuariService.findById(id);

        if (usuari.isPresent()) {
            usuariService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
