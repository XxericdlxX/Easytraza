package cat.copernic.easytraza_backend.security;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EasyTrazaUserDetailsService implements UserDetailsService {

    private final UsuariRepository usuariRepository;

    public EasyTrazaUserDetailsService(UsuariRepository usuariRepository) {
        this.usuariRepository = usuariRepository;
    }

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
