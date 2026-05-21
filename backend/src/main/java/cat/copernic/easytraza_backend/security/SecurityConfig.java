package cat.copernic.easytraza_backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuració principal de seguretat web d'EasyTraza.
 *
 * <p>
 * Defineix les rutes públiques, les rutes exclusives d'administració, la gestió
 * del formulari d'inici de sessió, el tancament de sessió i els components
 * d'error personalitzats.</p>
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;

    @Autowired
    private EasyTrazaAccessDeniedHandler easyTrazaAccessDeniedHandler;

    /**
     * Construeix la cadena de filtres de seguretat de l'aplicació.
     *
     * @param http configurador de seguretat HTTP proporcionat per Spring
     * Security
     * @return cadena de filtres aplicada a les peticions web
     * @throws Exception si la configuració de seguretat no es pot construir
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login",
                        "/recuperar-contrasenya",
                        "/restablir-contrasenya",
                        "/acces-denegat",
                        "/error",
                        "/css/**",
                        "/img/**",
                        "/js/**",
                        "/favicon.ico",
                        "/web/perfil/foto/**"
                ).permitAll()
                .requestMatchers("/api/test-connection", "/mobile-api/**").permitAll()
                .requestMatchers(
                        "/web/usuaris/**",
                        "/web/productes/**",
                        "/web/proveidors/**",
                        "/web/materies-primeres/**",
                        "/web/clients/**"
                ).hasRole("ADMIN")
                .requestMatchers("/", "/web/**").authenticated()
                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(easyTrazaAccessDeniedHandler)
                )
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureHandler(loginAuthenticationFailureHandler)
                .permitAll()
                )
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                );

        return http.build();
    }

    /**
     * Proporciona el codificador de contrasenyes utilitzat al backend.
     *
     * @return instància de BCrypt per protegir les contrasenyes
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
