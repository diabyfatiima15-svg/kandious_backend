package com.kandious.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://kandious-frontend.vercel.app"
        ));
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ===== AUTH PUBLIC =====
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers("/api/client-auth/**").permitAll()


                        // ===== DASHBOARD =====
                        .requestMatchers("/api/dashboard/notifications")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        .requestMatchers("/api/dashboard/admin")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/dashboard/vendeur")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        .requestMatchers("/api/dashboard/caissier")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")

                        // ===== UTILISATEURS =====
                        // Admin seulement
                        .requestMatchers("/api/utilisateurs/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== PRODUITS =====
                        // Lecture : Admin + Vendeur + Caissier
                        // (Caissier doit voir les produits pour créer une vente)
                        .requestMatchers(HttpMethod.GET, "/api/produits/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        // Écriture : Admin seulement
                        .requestMatchers(HttpMethod.POST, "/api/produits/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/produits/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/produits/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== CATEGORIES =====
                        // Lecture : Admin + Vendeur + Caissier
                        .requestMatchers(HttpMethod.GET, "/api/categories/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        // Écriture : Admin seulement
                        .requestMatchers(HttpMethod.POST, "/api/categories/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== FOURNISSEURS =====
                        // Lecture : Admin + Vendeur
                        .requestMatchers(HttpMethod.GET, "/api/fournisseurs/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        // Écriture : Admin seulement
                        .requestMatchers(HttpMethod.POST, "/api/fournisseurs/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/fournisseurs/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/fournisseurs/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== ACHATS =====
                        // Lecture : Admin + Vendeur
                        .requestMatchers(HttpMethod.GET, "/api/achats/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        // Création : Admin + Vendeur
                        .requestMatchers(HttpMethod.POST, "/api/achats/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        // Réceptionner + Annuler : Admin + Vendeur
                        .requestMatchers(HttpMethod.PUT, "/api/achats/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        // Supprimer : Admin seulement
                        .requestMatchers(HttpMethod.DELETE, "/api/achats/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== VENTES =====
                        // Lecture : Admin + Caissier
                        .requestMatchers(HttpMethod.GET, "/api/ventes/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")
                        // Création : Admin + Caissier
                        .requestMatchers(HttpMethod.POST, "/api/ventes/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")
                        // Annuler : Admin + Caissier
                        .requestMatchers(HttpMethod.PUT, "/api/ventes/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")
                        // Supprimer : Admin seulement
                        .requestMatchers(HttpMethod.DELETE, "/api/ventes/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== FACTURES =====
                        // Lecture : Admin + Caissier
                        .requestMatchers(HttpMethod.GET, "/api/factures/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")
                        // Supprimer : Admin seulement
                        .requestMatchers(HttpMethod.DELETE, "/api/factures/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== CLIENTS =====
                        // Lecture : tous
                        .requestMatchers(HttpMethod.GET, "/api/clients/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        // Création : Admin + Vendeur + Caissier
                        .requestMatchers(HttpMethod.POST, "/api/clients/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        // Modification : Admin + Vendeur seulement
                        .requestMatchers(HttpMethod.PUT, "/api/clients/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        // Suppression : Admin seulement
                        .requestMatchers(HttpMethod.DELETE, "/api/clients/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== LOGS =====
                        // Écriture manuelle : tous les rôles
                        // (DOIT être avant la règle générale /api/logs/**)
                        .requestMatchers(HttpMethod.POST, "/api/logs/manuel")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        // Lecture + suppression : Admin seulement
                        .requestMatchers("/api/logs/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== TOUT LE RESTE =====
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}