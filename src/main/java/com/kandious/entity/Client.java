package com.kandious.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Data
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String prenom;

    private String telephone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "points_fidelite")
    private Integer pointsFidelite = 0;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Vente> ventes;




}