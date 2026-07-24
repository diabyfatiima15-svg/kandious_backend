package com.kandious.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    private String prenom;

    @Pattern(regexp = "^[0-9+\\s-]{6,20}$", message = "Numéro de téléphone invalide")
    private String telephone;

    @Email(message = "Format d'email invalide")
    @Column(unique = true)
    private String email;

    @Column(name = "mot_de_passe")
    @JsonIgnore
    private String motDePasse;

    @Column(name = "compte_en_ligne")
    private Boolean compteEnLigne = false;

    @Column(name = "email_verifie")
    private Boolean emailVerifie = false;

    @Column(name = "token_verification")
    @JsonIgnore
    private String tokenVerification;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Min(value = 0, message = "Les points de fidélité ne peuvent pas être négatifs")
    @Column(name = "points_fidelite")
    private Integer pointsFidelite = 0;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Vente> ventes;

}