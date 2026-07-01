package com.kandious.service;

import com.kandious.entity.Produit;
import com.kandious.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;

    // Récupérer tous les produits
    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    // Récupérer un produit par id
    public Optional<Produit> findById(Long id) {
        return produitRepository.findById(id);
    }

    // Rechercher un produit par nom
    public List<Produit> findByNom(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom);
    }

    // Rechercher par code barres
    public Optional<Produit> findByCodeBarres(String codeBarres) {
        return produitRepository.findByCodeBarres(codeBarres);
    }

    // Produits par catégorie
    public List<Produit> findByCategorie(Long categorieId) {
        return produitRepository.findByCategorieId(categorieId);
    }

    // Produits en rupture de stock
    public List<Produit> findEnRupture() {
        return produitRepository.findByStatut(Produit.Statut.RUPTURE);
    }

    // Produits dont le stock est bas
    public List<Produit> findStockBas() {
        return produitRepository.findByQuantiteStockLessThanEqual(5);
    }

    // Valider les données d'un produit
    private void validerProduit(Produit produit) {
        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom du produit est obligatoire !");
        }
        if (produit.getPrixAchat() == null
                || produit.getPrixAchat().signum() < 0) {
            throw new RuntimeException("Le prix d'achat doit être positif !");
        }
        if (produit.getPrixVente() == null
                || produit.getPrixVente().signum() < 0) {
            throw new RuntimeException("Le prix de vente doit être positif !");
        }
        if (produit.getPrixVente().compareTo(produit.getPrixAchat()) < 0) {
            throw new RuntimeException(
                    "Le prix de vente ne peut pas être inférieur au prix d'achat !");
        }
        if (produit.getQuantiteStock() != null
                && produit.getQuantiteStock() < 0) {
            throw new RuntimeException(
                    "La quantité en stock ne peut pas être négative !");
        }
    }

    // Créer un produit
    public Produit save(Produit produit) {
        validerProduit(produit);
        return produitRepository.save(produit);
    }

    // Modifier un produit
    public Produit update(Long id, Produit produit) {
        validerProduit(produit);
        Produit existing = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable !"));
        existing.setNom(produit.getNom());
        existing.setDescription(produit.getDescription());
        existing.setPrixAchat(produit.getPrixAchat());
        existing.setPrixVente(produit.getPrixVente());
        existing.setQuantiteStock(produit.getQuantiteStock());
        existing.setTaille(produit.getTaille());
        existing.setCouleur(produit.getCouleur());
        existing.setPhoto(produit.getPhoto());
        existing.setStatut(produit.getStatut());
        existing.setCategorie(produit.getCategorie());
        return produitRepository.save(existing);
    }

    // Mettre à jour le stock
    public Produit updateStock(Long id, Integer quantite) {
        if (quantite == null) {
            throw new RuntimeException("La quantité est obligatoire !");
        }
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable !"));
        int nouveauStock = produit.getQuantiteStock() + quantite;
        if (nouveauStock < 0) {
            throw new RuntimeException("Stock insuffisant pour cette opération !");
        }
        produit.setQuantiteStock(nouveauStock);
        // Ne pas réactiver un produit désactivé manuellement
        if (produit.getStatut() != Produit.Statut.INACTIF) {
            produit.setStatut(nouveauStock == 0
                    ? Produit.Statut.RUPTURE
                    : Produit.Statut.DISPONIBLE);
        }
        return produitRepository.save(produit);
    }

    // Supprimer un produit
    public void delete(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit introuvable !");
        }
        produitRepository.deleteById(id);
    }
}