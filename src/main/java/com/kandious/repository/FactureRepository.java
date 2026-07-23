package com.kandious.repository;

import com.kandious.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository
        extends JpaRepository<Facture, Long> {

    Optional<Facture> findByNumero(String numero);

    List<Facture> findByVenteId(Long venteId);

    List<Facture> findByStatut(String statut);

    @Query("SELECT f FROM Facture f " +
            "LEFT JOIN FETCH f.vente v " +
            "LEFT JOIN FETCH v.client " +
            "LEFT JOIN FETCH v.venteDetails vd " +
            "LEFT JOIN FETCH vd.produit")
    List<Facture> findAllWithDetails();
}