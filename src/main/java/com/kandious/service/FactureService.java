package com.kandious.service;

import com.kandious.entity.Facture;
import com.kandious.entity.Vente;
import com.kandious.repository.FactureRepository;
import com.kandious.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;
    private final VenteRepository venteRepository;

    @Transactional
    public List<Facture> findAll() {
        List<Facture> factures = factureRepository.findAll();
        factures.forEach(f -> {
            if (f.getVente() != null) {
                // Force chargement client
                if (f.getVente().getClient() != null) {
                    f.getVente().getClient().getNom();
                    f.getVente().getClient().getPrenom();
                    f.getVente().getClient().getTelephone();
                    f.getVente().getClient().getEmail();
                }
                // Force chargement details
                if (f.getVente().getVenteDetails() != null) {
                    f.getVente().getVenteDetails().forEach(d -> {
                        if (d.getProduit() != null) {
                            d.getProduit().getNom();
                        }
                    });
                }
            }
        });
        return factures;
    }

    public Optional<Facture> findById(Long id) {
        return factureRepository.findById(id);
    }

    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    public Facture annuler(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Facture introuvable !"));
        if ("ANNULEE".equals(facture.getStatut())) {
            throw new RuntimeException("Cette facture est déjà annulée !");
        }
        facture.setStatut("ANNULEE");
        return factureRepository.save(facture);
    }

    public void delete(Long id) {
        factureRepository.deleteById(id);
    }

    public Facture regenerer(Long venteId) {
        List<Facture> facturesExistantes = factureRepository.findByVenteId(venteId);

        boolean aUneFactureActive = facturesExistantes.stream()
                .anyMatch(f -> !"ANNULEE".equals(f.getStatut()));

        if (aUneFactureActive) {
            throw new RuntimeException("Cette vente a déjà une facture active !");
        }

        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new RuntimeException("Vente introuvable !"));

        Facture nouvelleFacture = new Facture();
        nouvelleFacture.setNumero("FACT-" + System.currentTimeMillis());
        nouvelleFacture.setMontant(vente.getMontantTotal());
        nouvelleFacture.setStatut("EMISE");
        nouvelleFacture.setVente(vente);

        return factureRepository.save(nouvelleFacture);
    }
}