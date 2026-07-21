package com.kandious.repository;

import com.kandious.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository
        extends JpaRepository<Client, Long> {

    List<Client> findByNomContainingIgnoreCase(String nom);

    Boolean existsByEmail(String email);

    Boolean existsByTelephone(String telephone);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByTokenVerification(String token);
}