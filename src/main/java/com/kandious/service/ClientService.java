package com.kandious.service;

import com.kandious.entity.Client;
import com.kandious.repository.ClientRepository;
import com.kandious.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final VenteRepository venteRepository;

    // Récupérer tous les clients
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    // Récupérer un client par id
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    // Rechercher un client par nom
    public List<Client> findByNom(String nom) {
        return clientRepository.findByNomContainingIgnoreCase(nom);
    }

    // Créer un client
    public Client save(Client client) {
        if (client.getEmail() != null &&
                clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Cet email existe déjà !");
        }
        if (client.getTelephone() != null &&
                clientRepository.existsByTelephone(client.getTelephone())) {
            throw new RuntimeException("Ce téléphone existe déjà !");
        }
        return clientRepository.save(client);
    }

    // Modifier un client
    public Client update(Long id, Client client) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable !"));
        existing.setNom(client.getNom());
        existing.setPrenom(client.getPrenom());
        existing.setTelephone(client.getTelephone());
        existing.setEmail(client.getEmail());
        existing.setAdresse(client.getAdresse());
        return clientRepository.save(existing);
    }

    // Supprimer un client
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client introuvable !");
        }
        // Empêcher la suppression d'un client ayant des ventes liées
        if (!venteRepository.findByClientId(id).isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer ce client : des ventes lui sont associées.");
        }
        clientRepository.deleteById(id);
    }

    // Ajouter des points fidélité
    public Client ajouterPoints(Long id, Integer points) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable !"));
        client.setPointsFidelite(client.getPointsFidelite() + points);
        return clientRepository.save(client);
    }
}