package com.kandious.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_promo", unique = true)
    private String codePromo;

    @NotNull(message = "La réduction est obligatoire")
    @DecimalMin(value = "0.0", message = "La réduction ne peut pas être négative")
    @DecimalMax(value = "100.0", message = "La réduction ne peut pas dépasser 100%")
    private java.math.BigDecimal reduction;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id")
    private Produit produit;
}