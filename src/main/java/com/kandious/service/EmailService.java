package com.kandious.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String EMAIL_EXPEDITEUR = "diabyfatiima15@gmail.com";
    private static final String NOM_EXPEDITEUR = "KANDIOU'S Fashion";

    private void envoyerEmail(String destinataire, String sujet, String texte) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> sender = new HashMap<>();
        sender.put("name", NOM_EXPEDITEUR);
        sender.put("email", EMAIL_EXPEDITEUR);

        Map<String, Object> to = new HashMap<>();
        to.put("email", destinataire);

        Map<String, Object> body = new HashMap<>();
        body.put("sender", sender);
        body.put("to", new Object[]{to});
        body.put("subject", sujet);
        body.put("textContent", texte);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForObject(BREVO_URL, request, String.class);
        } catch (Exception e) {
            System.err.println("Erreur envoi email : " + e.getMessage());
        }
    }

    public void envoyerEmailVerification(String destinataire, String nom, String token) {
        String texte =
                "Bonjour " + nom + ",\n\n" +
                        "Merci de vous être inscrite sur KANDIOU'S Fashion !\n\n" +
                        "Pour activer votre compte, cliquez sur le lien ci-dessous :\n" +
                        frontendUrl + "/verifier-email?token=" + token + "\n\n" +
                        "Ce lien est valable 24 heures.\n\n" +
                        "Si vous n'êtes pas à l'origine de cette inscription, ignorez ce message.\n\n" +
                        "L'équipe KANDIOU'S Fashion";

        envoyerEmail(destinataire, "Confirmez votre compte - KANDIOU'S Fashion", texte);
    }

    public void notifierAdminNouveauClient(String emailAdmin, String nomClient) {
        String texte =
                "Bonjour,\n\n" +
                        "Une nouvelle cliente vient de s'inscrire sur la boutique en ligne :\n\n" +
                        nomClient + "\n\n" +
                        "L'équipe KANDIOU'S Fashion";

        envoyerEmail(emailAdmin, "Nouveau client inscrit - KANDIOU'S Fashion", texte);
    }

    public void notifierAdminNouvelleCommande(String emailAdmin, String numeroCommande, String nomClient) {
        String texte =
                "Bonjour,\n\n" +
                        "Une nouvelle commande vient d'être passée sur la boutique en ligne :\n\n" +
                        "Commande N° " + numeroCommande + "\n" +
                        "Cliente : " + nomClient + "\n\n" +
                        "Connectez-vous à l'espace d'administration pour la traiter.\n\n" +
                        "L'équipe KANDIOU'S Fashion";

        envoyerEmail(emailAdmin, "Nouvelle commande en ligne - KANDIOU'S Fashion", texte);
    }
}