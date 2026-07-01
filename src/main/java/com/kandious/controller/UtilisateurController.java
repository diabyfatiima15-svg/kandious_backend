package com.kandious.controller;

import com.kandious.entity.Utilisateur;
import com.kandious.repository.UtilisateurRepository;
import com.kandious.security.JwtUtils;
import com.kandious.service.LogActiviteService;
import com.kandious.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final LogActiviteService logActiviteService;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtUtils jwtUtils;

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

    // GET /api/utilisateurs
    @GetMapping
    public ResponseEntity<List<Utilisateur>> findAll() {
        return ResponseEntity.ok(utilisateurService.findAll());
    }

    // GET /api/utilisateurs/1
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> findById(@PathVariable Long id) {
        return utilisateurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/utilisateurs
    @PostMapping
    public ResponseEntity<Utilisateur> save(
            @RequestBody Utilisateur utilisateur,
            @RequestHeader("Authorization") String authHeader) {
        Utilisateur saved = utilisateurService.save(utilisateur);

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(userId, "CREATION_UTILISATEUR",
                    "Utilisateur créé : " + saved.getPrenom() + " "
                            + saved.getNom() + " (" + saved.getRole() + ")");
        }
        return ResponseEntity.ok(saved);
    }

    // PUT /api/utilisateurs/1
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> update(
            @PathVariable Long id,
            @RequestBody Utilisateur utilisateur,
            @RequestHeader("Authorization") String authHeader) {
        Long currentUserId = getUtilisateurId(authHeader);
        Utilisateur updated = utilisateurService.update(id, utilisateur, currentUserId);

        Long userId = currentUserId;
        if (userId != null) {
            logActiviteService.logAction(userId, "MODIFICATION_UTILISATEUR",
                    "Utilisateur modifié : " + updated.getPrenom() + " "
                            + updated.getNom() + " (" + updated.getRole() + ")");
        }
        return ResponseEntity.ok(updated);
    }

    // PUT /api/utilisateurs/1/mot-de-passe
    @PutMapping("/{id}/mot-de-passe")
    public ResponseEntity<Utilisateur> changerMotDePasse(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {
        Utilisateur updated = utilisateurService.changerMotDePasse(
                id, request.get("motDePasse"));

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            boolean propreCompte = userId.equals(id);
            logActiviteService.logAction(userId, "CHANGEMENT_MOT_DE_PASSE",
                    propreCompte
                            ? "Mot de passe personnel modifié"
                            : "Mot de passe modifié pour : "
                            + updated.getPrenom() + " " + updated.getNom());
        }
        return ResponseEntity.ok(updated);
    }

    // PUT /api/utilisateurs/1/toggle
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Utilisateur> toggleActif(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long currentUserId = getUtilisateurId(authHeader);
        Utilisateur updated = utilisateurService.toggleActif(id, currentUserId);

        Long userId = currentUserId;
        if (userId != null) {
            logActiviteService.logAction(userId,
                    updated.getActif() ? "ACTIVATION_UTILISATEUR" : "DESACTIVATION_UTILISATEUR",
                    (updated.getActif() ? "Compte activé : " : "Compte désactivé : ")
                            + updated.getPrenom() + " " + updated.getNom());
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/utilisateurs/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Utilisateur user = utilisateurService.findById(id).orElse(null);
        String nom = user != null
                ? user.getPrenom() + " " + user.getNom()
                : "ID " + id;

        utilisateurService.delete(id);

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(userId, "SUPPRESSION_UTILISATEUR",
                    "Utilisateur supprimé : " + nom);
        }
        return ResponseEntity.ok().build();
    }
}