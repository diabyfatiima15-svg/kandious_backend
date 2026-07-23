package com.kandious.service;

import com.kandious.dto.CommandeCreationDTO;
import com.kandious.dto.CommandeItemDTO;
import com.kandious.entity.*;
import com.kandious.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;
    private final ClientRepository clientRepository;
    private final EmailService emailService;

    private static final String EMAIL_ADMIN = "diabyfatiima15@gmail.com";

    @Transactional
    public Commande creerCommande(Long clientId, CommandeCreationDTO dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable !"));

        Commande commande = new Commande();
        commande.setClient(client);
        commande.setModeLivraison(dto.getModeLivraison());
        commande.setAdresseLivraison(dto.getAdresseLivraison());
        commande.setModePaiement(dto.getModePaiement());
        commande.setStatut("EN_ATTENTE");
        commande.setStatutPaiement("EN_ATTENTE");
        commande.setDateCommande(LocalDateTime.now());
        commande.setNumero("CMD-" + System.currentTimeMillis());

        List<CommandeDetail> details = new ArrayList<>();
        double montantTotal = 0.0;

        for (CommandeItemDTO item : dto.getArticles()) {
            Produit produit = produitRepository.findById(item.getProduitId())
                    .orElseThrow(() -> new RuntimeException(
                            "Produit introuvable : " + item.getProduitId()));

            if (produit.getQuantiteStock() < item.getQuantite()) {
                throw new RuntimeException(
                        "Stock insuffisant pour " + produit.getNom()
                                + " (disponible : " + produit.getQuantiteStock() + ")");
            }

            CommandeDetail detail = new CommandeDetail();
            detail.setProduit(produit);
            detail.setQuantite(item.getQuantite());
            detail.setPrixUnitaire(produit.getPrixVente().doubleValue());
            double sousTotal = produit.getPrixVente().doubleValue() * item.getQuantite();
            detail.setSousTotal(sousTotal);
            detail.setCommande(commande);

            details.add(detail);
            montantTotal += sousTotal;
        }

        commande.setCommandeDetails(details);
        commande.setMontantTotal(montantTotal);

        Commande saved = commandeRepository.save(commande);

        // Après
        emailService.notifierAdminNouvelleCommande(
                EMAIL_ADMIN,
                saved.getNumero(),
                client.getNom() + " " + client.getPrenom()
        );

        return saved;
    }

    public List<Commande> findByClientId(Long clientId) {
        return commandeRepository.findByClientId(clientId);
    }

    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    @Transactional
    public Commande confirmer(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable !"));

        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            throw new RuntimeException("Cette commande a déjà été traitée !");
        }

        for (CommandeDetail detail : commande.getCommandeDetails()) {
            Produit produit = detail.getProduit();
            if (produit.getQuantiteStock() < detail.getQuantite()) {
                throw new RuntimeException(
                        "Stock insuffisant pour " + produit.getNom());
            }
            produit.setQuantiteStock(produit.getQuantiteStock() - detail.getQuantite());
            produitRepository.save(produit);
        }

        commande.setStatut("CONFIRMEE");
        return commandeRepository.save(commande);
    }

    public Commande changerStatut(Long commandeId, String nouveauStatut) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande introuvable !"));
        commande.setStatut(nouveauStatut);
        return commandeRepository.save(commande);
    }
}