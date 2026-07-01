package com.kandious.repository;

import com.kandious.entity.VenteDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Repository
public interface VenteDetailRepository
        extends JpaRepository<VenteDetail, Long> {

    List<VenteDetail> findByVenteId(Long venteId);

    List<VenteDetail> findByProduitId(Long produitId);

    @Query("SELECT vd.produit.id, vd.produit.nom, " +
            "SUM(vd.quantite) as totalVendu " +
            "FROM VenteDetail vd " +
            "GROUP BY vd.produit.id, vd.produit.nom " +
            "ORDER BY totalVendu DESC")
    List<Object[]> findTopProduits();
    @Query("SELECT vd.produit.id, vd.produit.nom, SUM(vd.quantite) as total " +
            "FROM VenteDetail vd GROUP BY vd.produit.id, vd.produit.nom " +
            "ORDER BY total DESC")
    List<Object[]> findTopProduits(Pageable pageable);
}
