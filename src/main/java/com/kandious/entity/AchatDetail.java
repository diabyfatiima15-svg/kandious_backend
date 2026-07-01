package com.kandious.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "achats_details")
public class AchatDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire")
    private Double prixUnitaire;

    @Column(name = "sous_total")
    private Double sousTotal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "achat_id")
    @JsonIgnore
    private Achat achat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id")
    private Produit produit;
}