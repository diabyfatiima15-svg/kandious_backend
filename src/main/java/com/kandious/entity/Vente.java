package com.kandious.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventes")
@Data
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_vente")
    private LocalDateTime dateVente;

    @Column(name = "montant_ht")
    private Double montantHt;

    @Column(name = "montant_total")
    private Double montantTotal;

    private Double tva;
    private Double remise;

    @Column(name = "mode_paiement")
    private String modePaiement;

    private String statut;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "vente", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<VenteDetail> venteDetails = new ArrayList<>();

    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Facture> factures = new ArrayList<>();
}