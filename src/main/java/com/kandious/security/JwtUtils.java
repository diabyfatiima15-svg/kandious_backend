package com.kandious.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    // Générer un token JWT
    public String generateToken(String email, String role) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraire l'email depuis le token
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // Extraire le rôle depuis le token
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // Vérifier si le token est valide
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expiré");
        } catch (UnsupportedJwtException e) {
            System.out.println("Token non supporté");
        } catch (MalformedJwtException e) {
            System.out.println("Token malformé");
        } catch (SignatureException e) {
            System.out.println("Signature invalide");
        } catch (IllegalArgumentException e) {
            System.out.println("Token vide");
        }
        return false;
    }

    // Parser les claims du token
    private Claims parseClaims(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}