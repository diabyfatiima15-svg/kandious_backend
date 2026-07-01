package com.kandious.repository;

import com.kandious.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FournisseurRepository
        extends JpaRepository<Fournisseur, Long> {

    List<Fournisseur> findByNomContainingIgnoreCase(String nom);

    Boolean existsByEmail(String email);

    Boolean existsByTelephone(String telephone);
}