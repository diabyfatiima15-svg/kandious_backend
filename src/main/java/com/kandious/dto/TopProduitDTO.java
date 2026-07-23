package com.kandious.dto;

import lombok.Data;

@Data
public class TopProduitDTO {
    private String nomProduit;
    private Long quantiteVendue;
    private Double montantTotal;
}