package com.kandious.service;

import com.kandious.dto.RapportDTO;
import com.kandious.dto.TopProduitDTO;
import com.kandious.entity.Vente;
import com.kandious.entity.VenteDetail;
import com.kandious.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RapportService {

    private final VenteRepository venteRepository;

    public RapportDTO genererRapport(LocalDateTime debut, LocalDateTime fin) {
        List<Vente> ventes = venteRepository.findByDateVenteBetween(debut, fin)
                .stream()
                .filter(v -> "VALIDEE".equals(v.getStatut()))
                .collect(Collectors.toList());

        RapportDTO rapport = new RapportDTO();

        double ca = ventes.stream()
                .mapToDouble(Vente::getMontantTotal)
                .sum();
        rapport.setChiffreAffaires(ca);
        rapport.setNombreVentes((long) ventes.size());
        rapport.setPanierMoyen(ventes.isEmpty() ? 0.0 : ca / ventes.size());

        // Période précédente de même durée, pour comparaison
        long joursDifference = ChronoUnit.DAYS.between(debut, fin);
        LocalDateTime debutPrecedent = debut.minusDays(joursDifference + 1);
        LocalDateTime finPrecedent = debut.minusSeconds(1);

        List<Vente> ventesPrecedentes = venteRepository
                .findByDateVenteBetween(debutPrecedent, finPrecedent)
                .stream()
                .filter(v -> "VALIDEE".equals(v.getStatut()))
                .collect(Collectors.toList());

        double caPrecedent = ventesPrecedentes.stream()
                .mapToDouble(Vente::getMontantTotal)
                .sum();
        rapport.setChiffreAffairesPeriodePrecedente(caPrecedent);

        double evolution = caPrecedent == 0
                ? (ca > 0 ? 100.0 : 0.0)
                : ((ca - caPrecedent) / caPrecedent) * 100;
        rapport.setEvolutionPourcentage(evolution);

        // Top produits
        Map<String, Long> quantitesParProduit = new HashMap<>();
        Map<String, Double> montantsParProduit = new HashMap<>();

        for (Vente v : ventes) {
            for (VenteDetail d : v.getVenteDetails()) {
                if (d.getProduit() == null) continue;
                String nom = d.getProduit().getNom();
                quantitesParProduit.merge(nom, (long) d.getQuantite(), Long::sum);
                montantsParProduit.merge(nom, d.getSousTotal(), Double::sum);
            }
        }

        List<TopProduitDTO> topProduits = quantitesParProduit.entrySet().stream()
                .map(entry -> {
                    TopProduitDTO dto = new TopProduitDTO();
                    dto.setNomProduit(entry.getKey());
                    dto.setQuantiteVendue(entry.getValue());
                    dto.setMontantTotal(montantsParProduit.get(entry.getKey()));
                    return dto;
                })
                .sorted((a, b) -> Long.compare(b.getQuantiteVendue(), a.getQuantiteVendue()))
                .limit(10)
                .collect(Collectors.toList());

        rapport.setTopProduits(topProduits);

        return rapport;
    }
}