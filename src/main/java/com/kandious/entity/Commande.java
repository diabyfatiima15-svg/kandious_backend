package com.kandious.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Data
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", unique = true)
    private String numero;

    @Column(name = "date_commande")
    private LocalDateTime dateCommande = LocalDateTime.now();

    @Column(name = "montant_total")
    private Double montantTotal;

    @Column(name = "mode_livraison")
    private String modeLivraison;

    @Column(name = "adresse_livraison", columnDefinition = "TEXT")
    private String adresseLivraison;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @Column(name = "statut_paiement")
    private String statutPaiement = "EN_ATTENTE";

    private String statut = "EN_ATTENTE";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "commande", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CommandeDetail> commandeDetails = new ArrayList<>();

    @Column(name = "vente_id")
    private Long venteId;
}