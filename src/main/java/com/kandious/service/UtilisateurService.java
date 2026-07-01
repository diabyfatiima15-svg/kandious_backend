package com.kandious.service;

import com.kandious.entity.Utilisateur;
import com.kandious.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    // Récupérer tous les utilisateurs
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    // Récupérer un utilisateur par id
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    // Récupérer un utilisateur par email
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // Créer un utilisateur
    public Utilisateur save(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Cet email existe déjà !");
        }
        // Encoder le mot de passe avant de sauvegarder
        utilisateur.setMotDePasse(
                passwordEncoder.encode(utilisateur.getMotDePasse())
        );
        return utilisateurRepository.save(utilisateur);
    }

    // Modifier un utilisateur
    public Utilisateur update(Long id, Utilisateur utilisateur, Long currentUserId) {
        Utilisateur existing = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));
        // Empêcher un utilisateur de modifier son propre rôle
        if (currentUserId != null && currentUserId.equals(id)
                && existing.getRole() != null
                && !existing.getRole().equals(utilisateur.getRole())) {
            throw new RuntimeException(
                    "Vous ne pouvez pas modifier votre propre rôle !");
        }
        // Empêcher un utilisateur de désactiver son propre compte
        if (currentUserId != null && currentUserId.equals(id)
                && Boolean.FALSE.equals(utilisateur.getActif())) {
            throw new RuntimeException(
                    "Vous ne pouvez pas désactiver votre propre compte !");
        }
        existing.setNom(utilisateur.getNom());
        existing.setPrenom(utilisateur.getPrenom());
        existing.setEmail(utilisateur.getEmail());
        existing.setRole(utilisateur.getRole());
        existing.setActif(utilisateur.getActif());
        return utilisateurRepository.save(existing);
    }

    // Changer le mot de passe
    public Utilisateur changerMotDePasse(Long id, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));
        utilisateur.setMotDePasse(
                passwordEncoder.encode(nouveauMotDePasse)
        );
        return utilisateurRepository.save(utilisateur);
    }

    // Activer ou désactiver un utilisateur
    public Utilisateur toggleActif(Long id, Long currentUserId) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable !"));
        // Empêcher un utilisateur de désactiver son propre compte
        if (currentUserId != null && currentUserId.equals(id)
                && Boolean.TRUE.equals(utilisateur.getActif())) {
            throw new RuntimeException(
                    "Vous ne pouvez pas désactiver votre propre compte !");
        }
        utilisateur.setActif(!utilisateur.getActif());
        return utilisateurRepository.save(utilisateur);
    }

    // Supprimer un utilisateur
    public void delete(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable !");
        }
        utilisateurRepository.deleteById(id);
    }
}