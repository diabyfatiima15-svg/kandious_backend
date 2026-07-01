package com.kandious.repository;

import com.kandious.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository
        extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCodePromo(String codePromo);

    List<Promotion> findByActifTrue();

    List<Promotion> findByProduitId(Long produitId);

    List<Promotion> findByDateFinAfter(LocalDate date);
}