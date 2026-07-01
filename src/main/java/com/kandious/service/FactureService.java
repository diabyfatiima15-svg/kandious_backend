package com.kandious.service;

import com.kandious.entity.Facture;
import com.kandious.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;

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
}