package com.kandious.controller;

import com.kandious.entity.Utilisateur;
import com.kandious.entity.Vente;
import com.kandious.entity.VenteDetail;
import com.kandious.repository.UtilisateurRepository;
import com.kandious.security.JwtUtils;
import com.kandious.service.LogActiviteService;
import com.kandious.service.VenteService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService venteService;
    private final LogActiviteService logActiviteService;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtUtils jwtUtils;

    // Classe interne pour recevoir la requête
    @Data
    public static class VenteRequest {
        private Vente vente;
        private List<VenteDetail> details;
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
    public ResponseEntity<List<Vente>> findAll(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        // Un caissier ne voit que ses propres ventes
        boolean estCaissier = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_CAISSIER"::equals);
        if (estCaissier) {
            Long userId = getUtilisateurId(authHeader);
            if (userId != null) {
                return ResponseEntity.ok(
                        venteService.findByUtilisateur(userId));
            }
        }
        return ResponseEntity.ok(venteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vente> findById(@PathVariable Long id) {
        return venteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Vente>> findByClient(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(venteService.findByClient(clientId));
    }

    @GetMapping("/jour")
    public ResponseEntity<List<Vente>> findVentesJour() {
        return ResponseEntity.ok(venteService.findVentesduJour());
    }

    @PostMapping
    public ResponseEntity<Vente> creerVente(
            @RequestBody VenteRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Vente saved = venteService.creerVente(
                request.getVente(),
                request.getDetails()
        );

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            String clientNom = saved.getClient() != null
                    ? saved.getClient().getNom() + " "
                    + (saved.getClient().getPrenom() != null
                    ? saved.getClient().getPrenom() : "")
                    : "Client anonyme";
            logActiviteService.logAction(userId, "CREATION_VENTE",
                    "Vente #" + saved.getId() + " créée pour "
                            + clientNom + " - " + formatMontant(saved.getMontantTotal()));
        }
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<Vente> annulerVente(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Vente annulee = venteService.annulerVente(id);

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(userId, "ANNULATION_VENTE",
                    "Vente #" + annulee.getId() + " annulée - "
                            + formatMontant(annulee.getMontantTotal()));
        }
        return ResponseEntity.ok(annulee);
    }
}