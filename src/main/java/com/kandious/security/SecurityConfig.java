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
                        .requestMatchers("/api/client-auth/inscription", "/api/client-auth/verifier", "/api/client-auth/login").permitAll()
                        .requestMatchers("/api/client-auth/me").authenticated()

                        // ===== DASHBOARD =====
                        .requestMatchers("/api/dashboard/notifications")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        .requestMatchers("/api/dashboard/admin")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/dashboard/vendeur")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        .requestMatchers("/api/dashboard/caissier")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")

                        // ===== RAPPORTS (ADMIN uniquement) =====
                        .requestMatchers("/api/admin/rapports/**").hasAuthority("ROLE_ADMIN")

                        // ===== COMMANDES =====
                        .requestMatchers("/api/client/commandes/**").hasAuthority("ROLE_CLIENT")
                        .requestMatchers("/api/admin/commandes/**").hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")

                        // ===== HISTORIQUE CLIENT =====
                        .requestMatchers("/api/client/**").hasAuthority("ROLE_CLIENT")

                        // ===== UTILISATEURS =====
                        .requestMatchers("/api/utilisateurs/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== PRODUITS =====
                        .requestMatchers(HttpMethod.GET, "/api/produits/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        .requestMatchers(HttpMethod.POST, "/api/produits/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/produits/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/produits/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== CATEGORIES =====
                        .requestMatchers(HttpMethod.GET, "/api/categories/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        .requestMatchers(HttpMethod.POST, "/api/categories/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== FOURNISSEURS =====
                        .requestMatchers(HttpMethod.GET, "/api/fournisseurs/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        .requestMatchers(HttpMethod.POST, "/api/fournisseurs/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/fournisseurs/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/fournisseurs/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== ACHATS =====
                        .requestMatchers(HttpMethod.GET, "/api/achats/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        .requestMatchers(HttpMethod.POST, "/api/achats/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/achats/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/achats/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== VENTES =====
                        .requestMatchers(HttpMethod.GET, "/api/ventes/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")
                        .requestMatchers(HttpMethod.POST, "/api/ventes/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")
                        .requestMatchers(HttpMethod.PUT, "/api/ventes/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")
                        .requestMatchers(HttpMethod.DELETE, "/api/ventes/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== FACTURES =====
                        .requestMatchers(HttpMethod.GET, "/api/factures/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_CAISSIER")
                        .requestMatchers(HttpMethod.POST, "/api/factures/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/factures/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== CLIENTS (gestion interne) =====
                        .requestMatchers(HttpMethod.GET, "/api/clients/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        .requestMatchers(HttpMethod.POST, "/api/clients/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
                        .requestMatchers(HttpMethod.PUT, "/api/clients/**")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/clients/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== LOGS =====
                        .requestMatchers(HttpMethod.POST, "/api/logs/manuel")
                        .hasAnyAuthority("ROLE_ADMIN","ROLE_VENDEUR","ROLE_CAISSIER")
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