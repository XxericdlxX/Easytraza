package cat.copernic.easytraza_backend;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.Rol;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EasytrazaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasytrazaBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner crearSuperAdminInicial(UsuariRepository usuariRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            String emailSuperAdmin = "superadmin@easytraza.local";

            if (!usuariRepository.existsByEmail(emailSuperAdmin)) {
                Usuari superAdmin = new Usuari();
                superAdmin.setNom("Superadmin");
                superAdmin.setCognoms("Sistema");
                superAdmin.setRol(Rol.ADMIN);
                superAdmin.setEmail(emailSuperAdmin);
                superAdmin.setContrasenya(passwordEncoder.encode("superadmin"));

                usuariRepository.save(superAdmin);
            }
        };
    }
}
