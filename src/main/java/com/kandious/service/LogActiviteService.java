package com.kandious.service;

import com.kandious.entity.LogActivite;
import com.kandious.entity.Utilisateur;
import com.kandious.repository.LogActiviteRepository;
import com.kandious.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogActiviteService {

    private final LogActiviteRepository logActiviteRepository;
    private final UtilisateurRepository utilisateurRepository;

    // Récupérer tous les logs
    public List<LogActivite> findAll() {
        return logActiviteRepository.findAllByOrderByDateActionDesc();
    }

    // Logs par utilisateur
    public List<LogActivite> findByUtilisateur(Long utilisateurId) {
        return logActiviteRepository.findByUtilisateurId(utilisateurId);
    }

    // Enregistrer une action
    public LogActivite logAction(Long utilisateurId,
                                 String action,
                                 String details) {
        Utilisateur utilisateur = utilisateurRepository
                .findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));

        LogActivite log = new LogActivite();
        log.setAction(action);
        log.setDetails(details);
        log.setUtilisateur(utilisateur);

        return logActiviteRepository.save(log);
    }

    // Supprimer tous les logs
    public void deleteAll() {
        logActiviteRepository.deleteAll();
    }
}