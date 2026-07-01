package com.kandious.repository;

import com.kandious.entity.Achat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AchatRepository
        extends JpaRepository<Achat, Long> {

    List<Achat> findByFournisseurId(Long fournisseurId);

    long countByFournisseurId(Long fournisseurId);

    // Statut est maintenant un String
    List<Achat> findByStatut(String statut);

    List<Achat> findByDateAchatBetween(
            LocalDateTime debut,
            LocalDateTime fin
    );

    List<Achat> findByUtilisateurId(Long utilisateurId);
}