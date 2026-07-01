package com.kandious.controller;

import com.kandious.entity.Achat;
import com.kandious.entity.AchatDetail;
import com.kandious.entity.Utilisateur;
import com.kandious.repository.UtilisateurRepository;
import com.kandious.security.JwtUtils;
import com.kandious.service.AchatService;
import com.kandious.service.LogActiviteService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/achats")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AchatController {

    private final AchatService achatService;
    private final LogActiviteService logActiviteService;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtUtils jwtUtils;

    @Data
    public static class AchatRequest {
        private Achat achat;
        private List<AchatDetail> details;
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

    private String formatMontant(Object montant) {
        if (montant == null) return "0 GNF";
        double m = Double.parseDouble(montant.toString());
        return String.format("%,.0f", m).replace(",", " ") + " GNF";
    }

    @GetMapping
    public ResponseEntity<List<Achat>> findAll() {
        return ResponseEntity.ok(achatService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Achat> findById(@PathVariable Long id) {
        return achatService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    public ResponseEntity<List<Achat>> findByFournisseur(
            @PathVariable Long fournisseurId) {
        return ResponseEntity.ok(
                achatService.findByFournisseur(fournisseurId));
    }

    @PostMapping
    public ResponseEntity<Achat> creerAchat(
            @RequestBody AchatRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Achat saved = achatService.creerAchat(
                request.getAchat(),
                request.getDetails()
        );

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            String fournisseurNom = saved.getFournisseur() != null
                    ? saved.getFournisseur().getNom()
                    : "Fournisseur inconnu";
            logActiviteService.logAction(userId, "CREATION_ACHAT",
                    "Achat #" + saved.getId() + " créé chez "
                            + fournisseurNom + " - " + formatMontant(saved.getMontantTotal()));
        }
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/receptionner")
    public ResponseEntity<Achat> receptionnerAchat(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Achat receptionne = achatService.receptionnerAchat(id);

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(userId, "RECEPTION_ACHAT",
                    "Achat #" + receptionne.getId() + " réceptionné - "
                            + formatMontant(receptionne.getMontantTotal()));
        }
        return ResponseEntity.ok(receptionne);
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<Achat> annulerAchat(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Achat annule = achatService.annulerAchat(id);

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(userId, "ANNULATION_ACHAT",
                    "Achat #" + annule.getId() + " annulé - "
                            + formatMontant(annule.getMontantTotal()));
        }
        return ResponseEntity.ok(annule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        achatService.delete(id);

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(userId, "SUPPRESSION_ACHAT",
                    "Achat #" + id + " supprimé");
        }
        return ResponseEntity.ok().build();
    }
}