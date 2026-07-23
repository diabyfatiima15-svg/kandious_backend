package com.kandious.controller;

import com.kandious.entity.Commande;
import com.kandious.service.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/commandes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommandeAdminController {

    private final CommandeService commandeService;

    @GetMapping
    public ResponseEntity<List<Commande>> findAll() {
        return ResponseEntity.ok(commandeService.findAll());
    }

    @PutMapping("/{id}/confirmer")
    public ResponseEntity<?> confirmer(@PathVariable Long id) {
        try {
            Commande commande = commandeService.confirmer(id);
            return ResponseEntity.ok(commande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String nouveauStatut = body.get("statut");
            Commande commande = commandeService.changerStatut(id, nouveauStatut);
            return ResponseEntity.ok(commande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}