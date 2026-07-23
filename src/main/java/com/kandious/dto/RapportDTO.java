package com.kandious.dto;

import lombok.Data;
import java.util.List;

@Data
public class RapportDTO {
    private Double chiffreAffaires;
    private Long nombreVentes;
    private Double panierMoyen;
    private Double chiffreAffairesPeriodePrecedente;
    private Double evolutionPourcentage;
    private List<TopProduitDTO> topProduits;
}