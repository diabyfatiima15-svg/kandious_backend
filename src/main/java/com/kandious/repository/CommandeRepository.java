package com.kandious.repository;

import com.kandious.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByClientId(Long clientId);

    List<Commande> findByStatut(String statut);
}