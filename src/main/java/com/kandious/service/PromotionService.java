package com.kandious.service;

import com.kandious.entity.Promotion;
import com.kandious.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> findById(Long id) {
        return promotionRepository.findById(id);
    }

    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public Promotion update(Long id, Promotion promotion) {
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion introuvable !"));
        existing.setCodePromo(promotion.getCodePromo());
        existing.setReduction(promotion.getReduction());
        existing.setDateDebut(promotion.getDateDebut());
        existing.setDateFin(promotion.getDateFin());
        existing.setActif(promotion.getActif());
        existing.setProduit(promotion.getProduit());
        return promotionRepository.save(existing);
    }

    public void delete(Long id) {
        promotionRepository.deleteById(id);
    }

    public Optional<Promotion> getPromotionActive(Long produitId) {
        LocalDate aujourdhui = LocalDate.now();
        return promotionRepository
                .findByProduitIdAndActifTrueAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                        produitId, aujourdhui, aujourdhui
                );
    }
}