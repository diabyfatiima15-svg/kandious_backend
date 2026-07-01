package com.kandious.controller;

import com.kandious.entity.Utilisateur;
import com.kandious.repository.UtilisateurRepository;
import com.kandious.security.JwtUtils;
import com.kandious.service.LogActiviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final LogActiviteService logActiviteService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String motDePasse = request.get("motDePasse");

        // Vérifier que l'email et le mot de passe sont fournis
        if (email == null || motDePasse == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Email et mot de passe requis");
            return ResponseEntity.badRequest().body(error);
        }

        // Chercher l'utilisateur par email
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElse(null);

        // Vérifier que l'utilisateur existe
        if (utilisateur == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(401).body(error);
        }

        // Vérifier que le compte est actif
        if (!utilisateur.getActif()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Compte désactivé. Contactez l'administrateur");
            return ResponseEntity.status(403).body(error);
        }

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(401).body(error);
        }

        // Générer le token JWT
        String token = jwtUtils.generateToken(
                utilisateur.getEmail(),
                utilisateur.getRole().name()
        );

        // Enregistrer la connexion dans l'historique
        logActiviteService.logAction(utilisateur.getId(), "CONNEXION",
                utilisateur.getPrenom() + " " + utilisateur.getNom()
                        + " s'est connecté(e)");

        // Retourner le token et les infos utilisateur
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", utilisateur.getId());
        response.put("nom", utilisateur.getNom());
        response.put("prenom", utilisateur.getPrenom());
        response.put("email", utilisateur.getEmail());
        response.put("role", utilisateur.getRole().name());

        return ResponseEntity.ok(response);
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Utilisateur utilisateur) {

        // Vérifier que l'email n'existe pas déjà
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Cet email existe déjà");
            return ResponseEntity.badRequest().body(error);
        }

        // Encoder le mot de passe
        utilisateur.setMotDePasse(
                passwordEncoder.encode(utilisateur.getMotDePasse())
        );

        // Sauvegarder l'utilisateur
        Utilisateur saved = utilisateurRepository.save(utilisateur);

        // Générer le token JWT
        String token = jwtUtils.generateToken(
                saved.getEmail(),
                saved.getRole().name()
        );

        // Enregistrer l'inscription dans l'historique
        logActiviteService.logAction(saved.getId(), "INSCRIPTION",
                saved.getPrenom() + " " + saved.getNom()
                        + " s'est inscrit(e) (" + saved.getRole() + ")");

        // Retourner le token et les infos
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", saved.getId());
        response.put("nom", saved.getNom());
        response.put("prenom", saved.getPrenom());
        response.put("email", saved.getEmail());
        response.put("role", saved.getRole().name());

        return ResponseEntity.ok(response);
    }

    // GET /api/auth/me
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtils.getEmailFromToken(token);

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElse(null);

        if (utilisateur == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Utilisateur introuvable");
            return ResponseEntity.status(404).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", utilisateur.getId());
        response.put("nom", utilisateur.getNom());
        response.put("prenom", utilisateur.getPrenom());
        response.put("email", utilisateur.getEmail());
        response.put("role", utilisateur.getRole().name());

        return ResponseEntity.ok(response);
    }
}