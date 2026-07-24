package com.kandious.controller;

import com.kandious.entity.Produit;
import com.kandious.entity.Utilisateur;
import com.kandious.repository.UtilisateurRepository;
import com.kandious.security.JwtUtils;
import com.kandious.service.LogActiviteService;
import com.kandious.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final LogActiviteService logActiviteService;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtUtils jwtUtils;

    // Récupère l'ID de l'utilisateur connecté depuis le token
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

    // GET /api/produits
    @GetMapping
    public ResponseEntity<List<Produit>> findAll() {
        return ResponseEntity.ok(produitService.findAll());
    }

    // GET /api/produits/1
    @GetMapping("/{id}")
    public ResponseEntity<Produit> findById(@PathVariable Long id) {
        return produitService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/produits/search?nom=Robe
    @GetMapping("/search")
    public ResponseEntity<List<Produit>> search(
            @RequestParam String nom) {
        return ResponseEntity.ok(produitService.findByNom(nom));
    }

    // GET /api/produits/categorie/1
    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<Produit>> findByCategorie(
            @PathVariable Long categorieId) {
        return ResponseEntity.ok(
                produitService.findByCategorie(categorieId));
    }

    // GET /api/produits/rupture
    @GetMapping("/rupture")
    public ResponseEntity<List<Produit>> findEnRupture() {
        return ResponseEntity.ok(produitService.findEnRupture());
    }

    // GET /api/produits/stock-bas
    @GetMapping("/stock-bas")
    public ResponseEntity<List<Produit>> findStockBas() {
        return ResponseEntity.ok(produitService.findStockBas());
    }

    @PostMapping
    public ResponseEntity<?> save(
            @Valid @RequestBody Produit produit,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Produit saved = produitService.save(produit);

            Long userId = getUtilisateurId(authHeader);
            if (userId != null) {
                logActiviteService.logAction(userId, "CREATION_PRODUIT",
                        "Produit créé : " + saved.getNom());
            }
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // PUT /api/produits/1
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody Produit produit,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Produit updated = produitService.update(id, produit);

            Long userId = getUtilisateurId(authHeader);
            if (userId != null) {
                logActiviteService.logAction(userId, "MODIFICATION_PRODUIT",
                        "Produit modifié : " + updated.getNom());
            }
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // PUT /api/produits/1/stock?quantite=10
    @PutMapping("/{id}/stock")
    public ResponseEntity<Produit> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantite,
            @RequestHeader("Authorization") String authHeader) {
        Produit updated = produitService.updateStock(id, quantite);

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(userId, "MAJ_STOCK_PRODUIT",
                    "Stock modifié pour : " + updated.getNom()
                            + " (" + (quantite >= 0 ? "+" : "") + quantite + ")");
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/produits/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Produit produit = produitService.findById(id).orElse(null);
        String nom = produit != null ? produit.getNom() : "ID " + id;

        produitService.delete(id);

        Long userId = getUtilisateurId(authHeader);
        if (userId != null) {
            logActiviteService.logAction(userId, "SUPPRESSION_PRODUIT",
                    "Produit supprimé : " + nom);
        }
        return ResponseEntity.ok().build();
    }
}