package com.kandious.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(name = "date_facture")
    private LocalDateTime dateFacture = LocalDateTime.now();

    @Column(nullable = false)
    private Double montant;

    private String statut = "EMISE";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vente_id", nullable = false)
    private Vente vente;
}