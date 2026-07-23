package com.kandious.controller;

import com.kandious.dto.ClientInscriptionDTO;
import com.kandious.entity.Client;
import com.kandious.repository.ClientRepository;
import com.kandious.security.JwtUtils;
import com.kandious.service.ClientAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/client-auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClientAuthController {

    private final ClientAuthService clientAuthService;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(
            @Valid @RequestBody ClientInscriptionDTO dto) {
        try {
            clientAuthService.inscrire(dto);
            return ResponseEntity.ok(Map.of(
                    "message", "Compte créé ! Vérifiez votre email pour l'activer."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/verifier")
    public ResponseEntity<?> verifierEmail(
            @RequestParam String token) {
        try {
            clientAuthService.verifierEmail(token);
            return ResponseEntity.ok(Map.of(
                    "message", "Email vérifié ! Vous pouvez maintenant vous connecter."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String motDePasse = request.get("motDePasse");

        if (email == null || motDePasse == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Email et mot de passe requis");
            return ResponseEntity.badRequest().body(error);
        }

        Client client = clientRepository.findByEmail(email).orElse(null);

        if (client == null || !client.getCompteEnLigne()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(401).body(error);
        }

        if (!client.getEmailVerifie()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Veuillez vérifier votre email avant de vous connecter");
            return ResponseEntity.status(403).body(error);
        }

        if (!passwordEncoder.matches(motDePasse, client.getMotDePasse())) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(401).body(error);
        }

        String token = jwtUtils.generateToken(client.getEmail(), "CLIENT");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", client.getId());
        response.put("nom", client.getNom());
        response.put("prenom", client.getPrenom());
        response.put("email", client.getEmail());
        response.put("role", "CLIENT");
        response.put("pointsFidelite", client.getPointsFidelite());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtils.getEmailFromToken(token);

        Client client = clientRepository.findByEmail(email).orElse(null);

        if (client == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Client introuvable");
            return ResponseEntity.status(404).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", client.getId());
        response.put("nom", client.getNom());
        response.put("prenom", client.getPrenom());
        response.put("email", client.getEmail());
        response.put("role", "CLIENT");
        response.put("pointsFidelite", client.getPointsFidelite());

        return ResponseEntity.ok(response);
    }
}