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

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final String RESEND_URL = "https://api.resend.com/emails";
    private static final String EMAIL_EXPEDITEUR = "onboarding@resend.dev";

    private void envoyerEmail(String destinataire, String sujet, String texte) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("from", EMAIL_EXPEDITEUR);
        body.put("to", new String[]{destinataire});
        body.put("subject", sujet);
        body.put("text", texte);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForObject(RESEND_URL, request, String.class);
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
}