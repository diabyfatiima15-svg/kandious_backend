package com.kandious.controller;

import com.kandious.dto.CommandeCreationDTO;
import com.kandious.entity.Client;
import com.kandious.entity.Commande;
import com.kandious.repository.ClientRepository;
import com.kandious.security.JwtUtils;
import com.kandious.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/commandes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;
    private final ClientRepository clientRepository;
    private final JwtUtils jwtUtils;

    private Client getClientDepuisToken(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtils.getEmailFromToken(token);
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client introuvable !"));
    }

    @PostMapping
    public ResponseEntity<?> creerCommande(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CommandeCreationDTO dto) {
        try {
            Client client = getClientDepuisToken(authHeader);
            Commande commande = commandeService.creerCommande(client.getId(), dto);
            return ResponseEntity.ok(commande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> mesCommandes(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Client client = getClientDepuisToken(authHeader);
            List<Commande> commandes = commandeService.findByClientId(client.getId());
            return ResponseEntity.ok(commandes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}