package com.kandious.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "achats")
public class Achat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_achat")
    private LocalDateTime dateAchat = LocalDateTime.now();

    @Column(name = "montant_total")
    private Double montantTotal;

    private String statut = "EN_ATTENTE";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "achat", fetch = FetchType.EAGER)
    private List<AchatDetail> details;
}