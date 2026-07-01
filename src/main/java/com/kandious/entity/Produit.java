package com.kandious.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "produits")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "prix_achat", nullable = false)
    private BigDecimal prixAchat;

    @Column(name = "prix_vente", nullable = false)
    private BigDecimal prixVente;

    @Column(name = "quantite_stock")
    private Integer quantiteStock = 0;

    @Column(name = "stock_minimum")
    private Integer stockMinimum = 5;

    private String taille;

    private String couleur;

    @Column(name = "code_barres", unique = true)
    private String codeBarres;

    private String photo;

    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.DISPONIBLE;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    public enum Statut {
        DISPONIBLE, RUPTURE, INACTIF
    }
}