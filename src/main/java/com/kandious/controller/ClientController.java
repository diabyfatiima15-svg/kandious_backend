package com.kandious.controller;

import com.kandious.entity.Client;
import com.kandious.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // GET /api/clients
    @GetMapping
    public ResponseEntity<List<Client>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    // GET /api/clients/1
    @GetMapping("/{id}")
    public ResponseEntity<Client> findById(@PathVariable Long id) {
        return clientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/clients/search?nom=Camara
    @GetMapping("/search")
    public ResponseEntity<List<Client>> search(
            @RequestParam String nom) {
        return ResponseEntity.ok(clientService.findByNom(nom));
    }

    // POST /api/clients
    @PostMapping
    public ResponseEntity<Client> save(
            @RequestBody Client client) {
        return ResponseEntity.ok(clientService.save(client));
    }

    // PUT /api/clients/1
    @PutMapping("/{id}")
    public ResponseEntity<Client> update(
            @PathVariable Long id,
            @RequestBody Client client) {
        return ResponseEntity.ok(clientService.update(id, client));
    }

    // PUT /api/clients/1/points?points=10
    @PutMapping("/{id}/points")
    public ResponseEntity<Client> ajouterPoints(
            @PathVariable Long id,
            @RequestParam Integer points) {
        return ResponseEntity.ok(clientService.ajouterPoints(id, points));
    }

    // DELETE /api/clients/1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            clientService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}