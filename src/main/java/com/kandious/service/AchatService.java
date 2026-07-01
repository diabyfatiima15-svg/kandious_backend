package com.kandious.service;

import com.kandious.entity.*;
import com.kandious.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchatService {

    private final AchatRepository achatRepository;
    private final AchatDetailRepository achatDetailRepository;
    private final ProduitRepository produitRepository;

    public List<Achat> findAll() {
        return achatRepository.findAll();
    }

    public Optional<Achat> findById(Long id) {
        return achatRepository.findById(id);
    }

    public List<Achat> findByFournisseur(Long fournisseurId) {
        return achatRepository.findByFournisseurId(fournisseurId);
    }

    @Transactional
    public Achat creerAchat(Achat achat, List<AchatDetail> details) {

        // Valider les détails
        for (AchatDetail detail : details) {
            if (detail.getQuantite() == null || detail.getQuantite() <= 0)
                throw new RuntimeException(
                        "Quantité invalide pour un produit de l'achat");
            if (detail.getPrixUnitaire() == null || detail.getPrixUnitaire() < 0)
                throw new RuntimeException(
                        "Prix unitaire invalide pour un produit de l'achat");
        }

        // Calculer montant total
        double montantTotal = 0;
        for (AchatDetail detail : details) {
            double sousTotal = detail.getPrixUnitaire() * detail.getQuantite();
            detail.setSousTotal(sousTotal);
            montantTotal += sousTotal;
        }

        achat.setMontantTotal(montantTotal);
        achat.setStatut("EN_ATTENTE");
        achat.setDateAchat(LocalDateTime.now());

        // Sauvegarder l'achat
        Achat achatSauvegarde = achatRepository.save(achat);

        // Sauvegarder les détails
        for (AchatDetail detail : details) {
            detail.setAchat(achatSauvegarde);
            achatDetailRepository.save(detail);
        }

        return achatSauvegarde;
    }

    @Transactional
    public Achat receptionnerAchat(Long id) {
        Achat achat = achatRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Achat introuvable !"));

        if ("RECU".equals(achat.getStatut())) {
            throw new RuntimeException(
                    "Cet achat a déjà été réceptionné !");
        }

        if ("ANNULE".equals(achat.getStatut())) {
            throw new RuntimeException(
                    "Impossible de réceptionner un achat annulé !");
        }

        // Mettre à jour le stock
        List<AchatDetail> details =
                achatDetailRepository.findByAchatId(id);

        for (AchatDetail detail : details) {
            produitRepository
                    .findById(detail.getProduit().getId())
                    .ifPresent(produit -> {
                        produit.setQuantiteStock(
                                produit.getQuantiteStock() + detail.getQuantite());
                        produit.setStatut(Produit.Statut.DISPONIBLE);
                        produitRepository.save(produit);
                    });
        }

        achat.setStatut("RECU");
        return achatRepository.save(achat);
    }

    @Transactional
    public Achat annulerAchat(Long id) {
        Achat achat = achatRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Achat introuvable !"));

        if ("RECU".equals(achat.getStatut())) {
            throw new RuntimeException(
                    "Impossible d'annuler un achat déjà réceptionné !");
        }

        achat.setStatut("ANNULE");
        return achatRepository.save(achat);
    }

    @Transactional
    public void delete(Long id) {
        Achat achat = achatRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Achat introuvable !"));

        if (!"ANNULE".equals(achat.getStatut())) {
            throw new RuntimeException(
                    "Seul un achat annulé peut être supprimé !");
        }

        // Supprimer d'abord les détails liés
        List<AchatDetail> details =
                achatDetailRepository.findByAchatId(id);
        achatDetailRepository.deleteAll(details);

        // Puis supprimer l'achat
        achatRepository.deleteById(id);
    }
}