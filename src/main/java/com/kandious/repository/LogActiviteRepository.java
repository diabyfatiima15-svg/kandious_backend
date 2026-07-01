package com.kandious.repository;

import com.kandious.entity.LogActivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogActiviteRepository
        extends JpaRepository<LogActivite, Long> {

    List<LogActivite> findByUtilisateurId(Long utilisateurId);

    List<LogActivite> findByAction(String action);

    List<LogActivite> findByDateActionBetween(
            LocalDateTime debut,
            LocalDateTime fin
    );

    List<LogActivite> findAllByOrderByDateActionDesc();
}