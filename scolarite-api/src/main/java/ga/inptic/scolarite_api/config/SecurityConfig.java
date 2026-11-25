package ga.inptic.scolarite_api.config;

import ga.inptic.scolarite_api.repository.UtilisateurRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UtilisateurRepository utilisateurRepository) {
        return email -> utilisateurRepository.findByEmail(email)
                .map(u -> User.builder()
                        .username(u.getEmail())
                        .password(u.getMotDePasse())
                        .roles(u.getRole().name())
                        .disabled(!u.isActif())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                // Catalogue : lecture ouverte aux connectés, écriture réservée à SCOLARITE / DIRECTEUR_ETUDES
                .requestMatchers(HttpMethod.GET, "/api/catalogue/**").authenticated()
                .requestMatchers("/api/catalogue/**").hasAnyRole("SCOLARITE", "DIRECTEUR_ETUDES")
                // Inscriptions : SCOLARITE et DIRECTEUR_ETUDES
                .requestMatchers("/api/inscriptions/**").hasAnyRole("SCOLARITE", "DIRECTEUR_ETUDES")
                // Paiements & Impayés : COMPTABLE et DIRECTEUR_ETUDES
                .requestMatchers("/api/paiements/**").hasAnyRole("COMPTABLE", "DIRECTEUR_ETUDES")
                // Notes : délibération pour DIRECTEUR_ETUDES, relevé ouvert à tous (dont ETUDIANT)
                .requestMatchers("/api/notes/deliberer/**").hasRole("DIRECTEUR_ETUDES")
                .requestMatchers(HttpMethod.POST, "/api/notes").hasAnyRole("SCOLARITE", "DIRECTEUR_ETUDES")
                .requestMatchers(HttpMethod.GET, "/api/notes/releve/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}