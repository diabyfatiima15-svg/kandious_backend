package com.kandious.service;

import com.kandious.entity.Categorie;
import com.kandious.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

    // Récupérer toutes les catégories
    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    // Récupérer une catégorie par id
    public Optional<Categorie> findById(Long id) {
        return categorieRepository.findById(id);
    }

    // Créer ou modifier une catégorie
    public Categorie save(Categorie categorie) {
        if (categorieRepository.existsByNom(categorie.getNom())) {
            throw new RuntimeException("Cette catégorie existe déjà !");
        }
        return categorieRepository.save(categorie);
    }

    // Modifier une catégorie
    public Categorie update(Long id, Categorie categorie) {
        Categorie existing = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable !"));
        existing.setNom(categorie.getNom());
        existing.setDescription(categorie.getDescription());
        return categorieRepository.save(existing);
    }

    // Supprimer une catégorie
    public void delete(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Catégorie introuvable !"));

        if (categorie.getProduits() != null
                && !categorie.getProduits().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer : "
                            + categorie.getProduits().size()
                            + " produit(s) utilisent cette catégorie.");
        }

        categorieRepository.deleteById(id);
    }



}