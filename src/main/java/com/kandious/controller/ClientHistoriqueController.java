package com.kandious.controller;

import com.kandious.entity.Client;
import com.kandious.entity.Vente;
import com.kandious.repository.ClientRepository;
import com.kandious.repository.VenteRepository;
import com.kandious.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client/historique")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClientHistoriqueController {

    private final VenteRepository venteRepository;
    private final ClientRepository clientRepository;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> monHistorique(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtils.getEmailFromToken(token);

        Client client = clientRepository.findByEmail(email).orElse(null);

        if (client == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Client introuvable"));
        }

        List<Vente> ventes = venteRepository.findByClientId(client.getId())
                .stream()
                .sorted(Comparator.comparing(Vente::getDateVente).reversed())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ventes);
    }
}