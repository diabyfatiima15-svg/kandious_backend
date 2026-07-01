package com.kandious.repository;

import com.kandious.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository
        extends JpaRepository<Produit, Long> {

    List<Produit> findByNomContainingIgnoreCase(String nom);

    Optional<Produit> findByCodeBarres(String codeBarres);

    List<Produit> findByCategorieId(Long categorieId);

    List<Produit> findByStatut(Produit.Statut statut);

    List<Produit> findByQuantiteStockLessThanEqual(Integer stockMinimum);
}