package com.kandious.controller;

import com.kandious.entity.Fournisseur;
import com.kandious.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fournisseurs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public ResponseEntity<List<Fournisseur>> findAll() {
        return ResponseEntity.ok(fournisseurService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fournisseur> findById(@PathVariable Long id) {
        return fournisseurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Fournisseur>> search(
            @RequestParam String nom) {
        return ResponseEntity.ok(fournisseurService.findByNom(nom));
    }

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody Fournisseur fournisseur) {
        try {
            return ResponseEntity.ok(fournisseurService.save(fournisseur));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Fournisseur fournisseur) {
        try {
            return ResponseEntity.ok(
                    fournisseurService.update(id, fournisseur));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            fournisseurService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}