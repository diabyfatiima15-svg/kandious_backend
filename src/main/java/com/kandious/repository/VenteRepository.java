package com.kandious.repository;

import com.kandious.entity.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.query.Param;
@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {

    List<Vente> findByClientId(Long clientId);

    // Statut est un String pas un enum
    List<Vente> findByStatut(String statut);

    List<Vente> findByDateVenteBetween(
            LocalDateTime debut,
            LocalDateTime fin
    );

    List<Vente> findByUtilisateurId(Long utilisateurId);

    @Query("SELECT SUM(v.montantTotal) FROM Vente v " +
            "WHERE v.statut = 'VALIDEE' " +
            "AND v.dateVente BETWEEN :debut AND :fin")
    BigDecimal sumMontantTotalBetween(
            LocalDateTime debut,
            LocalDateTime fin
    );

    @Query("SELECT v FROM Vente v WHERE DATE(v.dateVente) = CURRENT_DATE")
    List<Vente> findVentesduJour();

    @Query("SELECT FUNCTION('DATE', v.dateVente), SUM(v.montantTotal) " +
            "FROM Vente v " +
            "WHERE v.dateVente >= :debut " +
            "AND v.statut = 'VALIDEE' " +
            "GROUP BY FUNCTION('DATE', v.dateVente) " +
            "ORDER BY FUNCTION('DATE', v.dateVente)")
    List<Object[]> findCaParJour(
            @Param("debut") LocalDateTime debut);
}