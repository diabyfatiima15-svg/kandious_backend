package com.kandious.controller;

import com.kandious.dto.ClientInscriptionDTO;
import com.kandious.service.ClientAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/client-auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClientAuthController {

    private final ClientAuthService clientAuthService;

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
}