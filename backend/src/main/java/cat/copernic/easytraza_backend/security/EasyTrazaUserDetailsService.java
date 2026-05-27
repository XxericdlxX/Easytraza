package cat.copernic.easytraza_backend.security;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servei d'integració entre els usuaris d'EasyTraza i Spring Security.
 *
 * <p>
 * Carrega l'usuari a partir del correu electrònic i construeix les dades de
 * seguretat necessàries perquè Spring Security pugui validar l'inici de
 * sessió.</p>
 */
@Service
public class EasyTrazaUserDetailsService implements UserDetailsService {

    private final UsuariRepository usuariRepository;

    /**
     * Crea el servei amb l'accés al repositori d'usuaris.
     *
     * @param usuariRepository repositori utilitzat per localitzar els usuaris
     */
    public EasyTrazaUserDetailsService(UsuariRepository usuariRepository) {
        this.usuariRepository = usuariRepository;
    }

    /**
     * Recupera les credencials i el rol de l'usuari associat al correu indicat.
     *
     * @param email correu electrònic introduït al formulari d'inici de sessió
     * @return dades de l'usuari adaptades al model de Spring Security
     * @throws UsernameNotFoundException si l'usuari no existeix o no té
     * contrasenya configurada
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String emailNormalitzat = email == null ? "" : email.trim().toLowerCase();

        Usuari usuari = usuariRepository.findByEmailIgnoreCase(emailNormalitzat)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat"));

        if (usuari.getContrasenya() == null || usuari.getContrasenya().isBlank()) {
            throw new UsernameNotFoundException("L'usuari no té contrasenya configurada");
        }

        return User.withUsername(usuari.getEmail())
                .password(usuari.getContrasenya())
                .roles(usuari.getRol().name())
                .build();
    }
}
