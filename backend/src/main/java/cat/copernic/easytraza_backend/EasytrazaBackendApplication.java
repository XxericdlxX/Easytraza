package cat.copernic.easytraza_backend;

import cat.copernic.easytraza_backend.model.Usuari;
import cat.copernic.easytraza_backend.model.enums.Rol;
import cat.copernic.easytraza_backend.repository.UsuariRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Component principal `EasytrazaBackendApplication` del projecte EasyTraza.
 */
@SpringBootApplication
public class EasytrazaBackendApplication {

    private static final String SUPERADMIN_EMAIL = "superadmin@easytraza.local";
    private static final String SUPERADMIN_DNI_VALID = "00000000T";
    private static final String SUPERADMIN_FOTO = "superadmin-logo-easytraza.png";

    /**
     * Executa l'operació `main`.
     *
     * @param args paràmetre necessari per a l'operació.
     */
    public static void main(String[] args) {
        SpringApplication.run(EasytrazaBackendApplication.class, args);
    }

    /**
     * Executa l'operació `crearSuperAdminInicial`.
     *
     * @param usuariRepository paràmetre necessari per a l'operació.
     * @param passwordEncoder paràmetre necessari per a l'operació.
     * @param perfilFotosPath paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Bean
    CommandLineRunner crearSuperAdminInicial(
            UsuariRepository usuariRepository,
            PasswordEncoder passwordEncoder,
            @Value("${perfil.fotos.path:../uploads/perfils}") String perfilFotosPath) {

        return args -> {
            Usuari superAdmin = usuariRepository.findByEmailIgnoreCase(SUPERADMIN_EMAIL)
                    .orElseGet(Usuari::new);

            boolean esNou = superAdmin.getId() == null;
            boolean modificat = false;

            if (esNou) {
                superAdmin.setNom("Superadmin");
                superAdmin.setCognoms("Sistema");
                superAdmin.setRol(Rol.ADMIN);
                superAdmin.setEmail(SUPERADMIN_EMAIL);
                superAdmin.setContrasenya(passwordEncoder.encode("superadmin"));
                modificat = true;
            }

            if (superAdmin.getNif() == null || superAdmin.getNif().isBlank()) {
                superAdmin.setNif(SUPERADMIN_DNI_VALID);
                modificat = true;
            }

            prepararLogoSuperAdmin(perfilFotosPath);

            if (!SUPERADMIN_FOTO.equals(superAdmin.getFotoPerfilNom())) {
                superAdmin.setFotoPerfilNom(SUPERADMIN_FOTO);
                modificat = true;
            }

            if (modificat) {
                usuariRepository.save(superAdmin);
            }
        };
    }

    /**
     * Executa l'operació `prepararLogoSuperAdmin`.
     *
     * @param perfilFotosPath paràmetre necessari per a l'operació.
     */
    private void prepararLogoSuperAdmin(String perfilFotosPath) {
        try {
            Path directori = Paths.get(perfilFotosPath).toAbsolutePath().normalize();
            Files.createDirectories(directori);

            Path desti = directori.resolve(SUPERADMIN_FOTO).normalize();
            if (!desti.startsWith(directori)) {
                throw new IllegalStateException("La ruta de la foto del superadmin no és vàlida.");
            }

            ClassPathResource logoEasyTraza = new ClassPathResource("static/img/logo-easytraza.png");
            try (InputStream input = logoEasyTraza.getInputStream()) {
                Files.copy(input, desti, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No s'ha pogut preparar la foto de perfil del superadmin.", ex);
        }
    }
}
