package com.kandious.service;

import com.kandious.entity.*;
import com.kandious.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenteService {

    private final VenteRepository venteRepository;
    private final VenteDetailRepository venteDetailRepository;
    private final ProduitRepository produitRepository;
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;

    public List<Vente> findAll() {
        return venteRepository.findAll();
    }

    public Optional<Vente> findById(Long id) {
        return venteRepository.findById(id);
    }

    public List<Vente> findByClient(Long clientId) {
        return venteRepository.findByClientId(clientId);
    }

    public List<Vente> findByUtilisateur(Long utilisateurId) {
        return venteRepository.findByUtilisateurId(utilisateurId);
    }

    public List<Vente> findVentesduJour() {
        LocalDateTime debut = LocalDateTime.now()
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = LocalDateTime.now()
                .withHour(23).withMinute(59).withSecond(59);
        return venteRepository.findByDateVenteBetween(debut, fin);
    }

    public BigDecimal chiffreAffaires(
            LocalDateTime debut, LocalDateTime fin) {
        BigDecimal ca = venteRepository
                .sumMontantTotalBetween(debut, fin);
        return ca != null ? ca : BigDecimal.ZERO;
    }

    @Transactional
    public Vente creerVente(Vente vente, List<VenteDetail> details) {

        // Vérifier le stock
        for (VenteDetail detail : details) {
            Produit produit = produitRepository
                    .findById(detail.getProduit().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Produit introuvable !"));

            if (produit.getQuantiteStock() < detail.getQuantite()) {
                throw new RuntimeException(
                        "Stock insuffisant pour : " + produit.getNom());
            }
        }

        // Calculer montant HT
        double montantHt = 0;
        for (VenteDetail detail : details) {
            double sousTotal = detail.getPrixUnitaire()
                    * detail.getQuantite();
            detail.setSousTotal(sousTotal);
            montantHt += sousTotal;
        }

        // Appliquer TVA et remise
        double tva = vente.getTva() != null ? vente.getTva() : 18.0;
        double remise = vente.getRemise() != null
                ? vente.getRemise() : 0.0;

        double montantApresRemise = montantHt
                - (montantHt * remise / 100);
        double montantTotal = montantApresRemise
                + (montantApresRemise * tva / 100);

        vente.setMontantHt(montantHt);
        vente.setMontantTotal(montantTotal);
        vente.setDateVente(LocalDateTime.now());
        vente.setStatut("VALIDEE");

        // Sauvegarder la vente
        Vente venteSauvegardee = venteRepository.save(vente);

        // Sauvegarder les détails et mettre à jour le stock
        for (VenteDetail detail : details) {
            detail.setVente(venteSauvegardee);
            venteDetailRepository.save(detail);

            Produit produit = produitRepository
                    .findById(detail.getProduit().getId()).get();
            produit.setQuantiteStock(
                    produit.getQuantiteStock() - detail.getQuantite());

            if (produit.getQuantiteStock() <= 0) {
                produit.setStatut(Produit.Statut.RUPTURE);
            }
            produitRepository.save(produit);
        }

        // Points fidélité
        if (vente.getClient() != null
                && vente.getClient().getId() != null) {
            clientRepository.findById(vente.getClient().getId())
                    .ifPresent(client -> {
                        int points = (int)(montantTotal / 1000);
                        client.setPointsFidelite(
                                client.getPointsFidelite() + points);
                        clientRepository.save(client);
                    });
        }

        // Générer facture automatiquement
        Facture facture = new Facture();
        facture.setNumero("FACT-"
                + LocalDateTime.now().getYear()
                + "-"
                + String.format("%03d",
                factureRepository.count() + 1));
        facture.setMontant(montantTotal);
        facture.setVente(venteSauvegardee);
        facture.setDateFacture(LocalDateTime.now());
        facture.setStatut("PAYEE");
        factureRepository.save(facture);

        return venteSauvegardee;
    }

    @Transactional
    public Vente annulerVente(Long id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vente introuvable !"));

        if ("ANNULEE".equals(vente.getStatut())) {
            throw new RuntimeException(
                    "Cette vente est déjà annulée !");
        }

        // Remettre le stock
        List<VenteDetail> details = venteDetailRepository
                .findByVenteId(id);
        for (VenteDetail detail : details) {
            produitRepository
                    .findById(detail.getProduit().getId())
                    .ifPresent(produit -> {
                        produit.setQuantiteStock(
                                produit.getQuantiteStock()
                                        + detail.getQuantite());
                        produit.setStatut(Produit.Statut.DISPONIBLE);
                        produitRepository.save(produit);
                    });
        }

        // Synchroniser le statut de la facture liée
        factureRepository.findByVenteId(id)
                .ifPresent(facture -> {
                    facture.setStatut("ANNULEE");
                    factureRepository.save(facture);
                });

        vente.setStatut("ANNULEE");
        return venteRepository.save(vente);
    }
}