package com.kandious.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Le prix d'achat est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix d'achat ne peut pas être négatif")
    @Column(name = "prix_achat", nullable = false)
    private BigDecimal prixAchat;

    @NotNull(message = "Le prix de vente est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix de vente ne peut pas être négatif")
    @Column(name = "prix_vente", nullable = false)
    private BigDecimal prixVente;

    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Column(name = "quantite_stock")
    private Integer quantiteStock = 0;

    @Min(value = 0, message = "Le seuil minimum ne peut pas être négatif")
    @Column(name = "stock_minimum")
    private Integer stockMinimum = 5;

    private String taille;

    private String couleur;

    @Column(name = "code_barres", unique = true)
    private String codeBarres;

    @Column(columnDefinition = "LONGTEXT")
    private String photo;

    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.DISPONIBLE;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout = LocalDateTime.now();

    @NotNull(message = "La catégorie est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    public enum Statut {
        DISPONIBLE, RUPTURE, INACTIF
    }
}