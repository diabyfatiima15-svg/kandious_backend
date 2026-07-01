package com.kandious.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String telephone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout = LocalDateTime.now();
}