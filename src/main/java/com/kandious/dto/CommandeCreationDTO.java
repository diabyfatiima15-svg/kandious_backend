package com.kandious.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class CommandeCreationDTO {

    @NotEmpty(message = "Le panier ne peut pas être vide")
    private List<CommandeItemDTO> articles;

    @NotBlank(message = "Le mode de livraison est obligatoire")
    private String modeLivraison;

    private String adresseLivraison;

    @NotBlank(message = "Le mode de paiement est obligatoire")
    private String modePaiement;
}