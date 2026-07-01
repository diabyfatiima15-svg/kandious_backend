package com.kandious.security;

import com.kandious.entity.Utilisateur;
import com.kandious.repository.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Récupérer le header Authorization
        String authHeader = request.getHeader("Authorization");

        // Vérifier que le header existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le token (enlever "Bearer ")
        String token = authHeader.substring(7);

        // Valider le token
        if (!jwtUtils.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire l'email et le rôle
        String email = jwtUtils.getEmailFromToken(token);
        String role = jwtUtils.getRoleFromToken(token);

        // Vérifier que l'utilisateur existe en base
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email).orElse(null);

        if (utilisateur == null || !utilisateur.getActif()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Créer l'authentification Spring Security
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        // Mettre l'authentification dans le contexte
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}