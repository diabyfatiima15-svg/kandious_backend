package com.kandious.controller;

import com.kandious.entity.LogActivite;
import com.kandious.entity.Utilisateur;
import com.kandious.repository.UtilisateurRepository;
import com.kandious.security.JwtUtils;
import com.kandious.service.LogActiviteService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LogActiviteController {

    private final LogActiviteService logActiviteService;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtUtils jwtUtils;

    @Data
    public static class LogManuelRequest {
        private String action;
        private String details;
    }

    private Long getUtilisateurId(String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);
            Utilisateur user = utilisateurRepository
                    .findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // GET /api/logs
    @GetMapping
    public ResponseEntity<List<LogActivite>> findAll() {
        return ResponseEntity.ok(logActiviteService.findAll());
    }

    // GET /api/logs/utilisateur/1
    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<List<LogActivite>> findByUtilisateur(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                logActiviteService.findByUtilisateur(id));
    }

    // POST /api/logs/manuel
    // Pour les actions générées côté frontend (PDF, Excel...)
    @PostMapping("/manuel")
    public ResponseEntity<Void> logManuel(
            @RequestBody LogManuelRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(
                    userId, request.getAction(), request.getDetails());
        }
        return ResponseEntity.ok().build();
    }

    // DELETE /api/logs
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        logActiviteService.deleteAll();
        return ResponseEntity.ok().build();
    }
}