package com.kandious.service;

import com.kandious.entity.Produit;
import com.kandious.entity.Vente;
import com.kandious.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VenteRepository venteRepository;
    private final ProduitRepository produitRepository;
    private final ClientRepository clientRepository;
    private final FactureRepository factureRepository;
    private final VenteDetailRepository venteDetailRepository;

    public Map<String, Object> getStatsAdmin() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime debutJour = LocalDateTime.now()
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finJour = LocalDateTime.now()
                .withHour(23).withMinute(59).withSecond(59);
        LocalDateTime debutMois = LocalDateTime.now()
                .withDayOfMonth(1).withHour(0).withMinute(0);

        stats.put("totalProduits", produitRepository.count());
        stats.put("totalClients", clientRepository.count());
        stats.put("totalFactures", factureRepository.count());

        List<Vente> ventesJour = venteRepository
                .findByDateVenteBetween(debutJour, finJour);
        stats.put("ventesJour", ventesJour.size());
        stats.put("listeVentesJour", ventesJour);

        BigDecimal caJour = venteRepository
                .sumMontantTotalBetween(debutJour, finJour);
        stats.put("caJour",
                caJour != null ? caJour : BigDecimal.ZERO);

        BigDecimal caMois = venteRepository
                .sumMontantTotalBetween(debutMois, finJour);
        stats.put("caMois",
                caMois != null ? caMois : BigDecimal.ZERO);

        List<Produit> ruptures = produitRepository
                .findByStatut(Produit.Statut.RUPTURE);
        stats.put("produitsEnRupture", ruptures.size());

        List<Produit> stockBas = produitRepository
                .findByQuantiteStockLessThanEqual(5);
        stats.put("produitsStockBas", stockBas.size());

        List<Object[]> topProduits = venteDetailRepository
                .findTopProduits(PageRequest.of(0, 5));
        stats.put("topProduits", topProduits);

        // CA 7 jours
        LocalDateTime debut7Jours = LocalDateTime.now()
                .minusDays(6)
                .withHour(0).withMinute(0).withSecond(0);
        List<Object[]> caParJour = venteRepository
                .findCaParJour(debut7Jours);
        List<Map<String, Object>> ca7Jours = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime jour = LocalDateTime.now().minusDays(i);
            String dateStr = jour.toLocalDate().toString();
            double montant = caParJour.stream()
                    .filter(row -> row[0].toString()
                            .startsWith(dateStr))
                    .mapToDouble(row ->
                            ((Number) row[1]).doubleValue())
                    .sum();
            Map<String, Object> jourData = new HashMap<>();
            jourData.put("date", dateStr);
            jourData.put("jour", jour.getDayOfWeek()
                    .toString().substring(0, 3));
            jourData.put("montant", montant);
            ca7Jours.add(jourData);
        }
        stats.put("ca7Jours", ca7Jours);

        return stats;
    }

    public Map<String, Object> getStatsVendeur() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProduits", produitRepository.count());

        List<Produit> ruptures = produitRepository
                .findByStatut(Produit.Statut.RUPTURE);
        stats.put("produitsEnRupture", ruptures.size());
        stats.put("listeRuptures", ruptures);

        List<Produit> stockBas = produitRepository
                .findByQuantiteStockLessThanEqual(5);
        stats.put("produitsStockBas", stockBas.size());
        stats.put("listeStockBas", stockBas);

        List<Object[]> topProduits = venteDetailRepository
                .findTopProduits(PageRequest.of(0, 5));
        stats.put("topProduits", topProduits);

        return stats;
    }

    public Map<String, Object> getStatsCaissier(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime debutJour = LocalDateTime.now()
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finJour = LocalDateTime.now()
                .withHour(23).withMinute(59).withSecond(59);

        // Un caissier ne voit que les ventes qu'il a lui-même effectuées
        List<Vente> ventesJour = venteRepository
                .findByDateVenteBetween(debutJour, finJour)
                .stream()
                .filter(v -> userId == null
                        || (v.getUtilisateur() != null
                        && userId.equals(v.getUtilisateur().getId())))
                .toList();
        stats.put("ventesJour", ventesJour.size());
        stats.put("listeVentesJour", ventesJour);

        stats.put("facturesJour", ventesJour.stream()
                .filter(v -> "VALIDEE".equals(v.getStatut()))
                .count());

        List<Object[]> topProduits = venteDetailRepository
                .findTopProduits(PageRequest.of(0, 3));
        stats.put("topProduits", topProduits);

        return stats;
    }

    // ============================================
    // NOTIFICATIONS RÉELLES
    // ============================================
    public Map<String, Object> getNotifications(String role) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> notifications = new ArrayList<>();

        LocalDateTime debutJour = LocalDateTime.now()
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finJour = LocalDateTime.now()
                .withHour(23).withMinute(59).withSecond(59);

        // 1. Produits en rupture
        List<Produit> ruptures = produitRepository
                .findByStatut(Produit.Statut.RUPTURE);
        if (!ruptures.isEmpty()) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("type", "RUPTURE");
            notif.put("icon", "🔴");
            notif.put("titre", ruptures.size()
                    + " produit(s) en rupture de stock");
            notif.put("detail", ruptures.stream()
                    .map(Produit::getNom)
                    .limit(3)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse(""));
            notif.put("couleur", "#c62828");
            notif.put("lien", "/produits");
            notifications.add(notif);
        }

        // 2. Produits stock bas
        List<Produit> stockBas = produitRepository
                .findByQuantiteStockLessThanEqual(5)
                .stream()
                .filter(p -> p.getQuantiteStock() > 0)
                .toList();
        if (!stockBas.isEmpty()) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("type", "STOCK_BAS");
            notif.put("icon", "🟡");
            notif.put("titre", stockBas.size()
                    + " produit(s) avec stock bas");
            notif.put("detail",
                    "Stock inférieur ou égal à 5 unités");
            notif.put("couleur", "#e65100");
            notif.put("lien", "/produits");
            notifications.add(notif);
        }

        // 3. CA du jour (réservé à l'ADMIN)
        BigDecimal caJour = venteRepository
                .sumMontantTotalBetween(debutJour, finJour);
        double ca = caJour != null ? caJour.doubleValue() : 0;
        if (ca > 0 && "ADMIN".equals(role)) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("type", "CA_JOUR");
            notif.put("icon", "🟢");
            notif.put("titre", "CA du jour : "
                    + String.format("%,.0f", ca)
                    .replace(",", " ")
                    + " GNF");
            List<Vente> ventesJour = venteRepository
                    .findByDateVenteBetween(debutJour, finJour);
            notif.put("detail", ventesJour.size()
                    + " vente(s) effectuée(s) aujourd'hui");
            notif.put("couleur", "#2e7d32");
            notif.put("lien", "/ventes");
            notifications.add(notif);
        }

        // 4. Aucune vente aujourd'hui
        if (ca == 0) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("type", "PAS_VENTE");
            notif.put("icon", "ℹ️");
            notif.put("titre", "Aucune vente aujourd'hui");
            notif.put("detail",
                    "Commencez à enregistrer des ventes");
            notif.put("couleur", "#1565c0");
            notif.put("lien", "/ventes");
            notifications.add(notif);
        }

        result.put("notifications", notifications);
        result.put("total", notifications.size());
        return result;
    }
}