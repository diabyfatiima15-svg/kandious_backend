package com.kandious.controller;

import com.kandious.entity.Produit;
import com.kandious.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/produits")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProduitPublicController {

    private final ProduitService produitService;

    @GetMapping
    public ResponseEntity<List<Produit>> findAllDisponibles() {
        List<Produit> disponibles = produitService.findAll().stream()
                .filter(p -> p.getStatut() == Produit.Statut.DISPONIBLE)
                .collect(Collectors.toList());
        return ResponseEntity.ok(disponibles);
    }
}