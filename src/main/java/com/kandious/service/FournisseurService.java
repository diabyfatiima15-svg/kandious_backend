package com.kandious.service;

import com.kandious.entity.Fournisseur;
import com.kandious.repository.AchatRepository;
import com.kandious.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final AchatRepository achatRepository;

    // Récupérer tous les fournisseurs
    public List<Fournisseur> findAll() {
        return fournisseurRepository.findAll();
    }

    // Récupérer un fournisseur par id
    public Optional<Fournisseur> findById(Long id) {
        return fournisseurRepository.findById(id);
    }

    // Rechercher un fournisseur par nom
    public List<Fournisseur> findByNom(String nom) {
        return fournisseurRepository.findByNomContainingIgnoreCase(nom);
    }

    // Créer un fournisseur
    public Fournisseur save(Fournisseur fournisseur) {
        if (fournisseur.getEmail() != null &&
                fournisseurRepository.existsByEmail(fournisseur.getEmail())) {
            throw new RuntimeException("Cet email existe déjà !");
        }
        if (fournisseur.getTelephone() != null &&
                fournisseurRepository.existsByTelephone(fournisseur.getTelephone())) {
            throw new RuntimeException("Ce téléphone existe déjà !");
        }
        return fournisseurRepository.save(fournisseur);
    }

    // Modifier un fournisseur
    public Fournisseur update(Long id, Fournisseur fournisseur) {
        Fournisseur existing = fournisseurRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Fournisseur introuvable !"));
        existing.setNom(fournisseur.getNom());
        existing.setTelephone(fournisseur.getTelephone());
        existing.setEmail(fournisseur.getEmail());
        existing.setAdresse(fournisseur.getAdresse());
        return fournisseurRepository.save(existing);
    }

    // Supprimer un fournisseur
    public void delete(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Fournisseur introuvable !"));

        // Vérifier si des achats existent pour ce fournisseur
        long nbAchats = achatRepository.countByFournisseurId(id);
        if (nbAchats > 0) {
            throw new RuntimeException(
                    "Impossible de supprimer : "
                            + nbAchats + " achat(s) sont liés à ce fournisseur. "
                            + "La suppression compromettrait l'historique des approvisionnements.");
        }

        fournisseurRepository.deleteById(id);
    }
}