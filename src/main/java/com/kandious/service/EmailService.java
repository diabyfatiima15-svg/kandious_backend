package com.kandious.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailExpediteur;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void envoyerEmailVerification(String destinataire, String nom, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailExpediteur);
        message.setTo(destinataire);
        message.setSubject("Confirmez votre compte - KANDIOU'S Fashion");
        message.setText(
                "Bonjour " + nom + ",\n\n" +
                        "Merci de vous être inscrite sur KANDIOU'S Fashion !\n\n" +
                        "Pour activer votre compte, cliquez sur le lien ci-dessous :\n" +
                        frontendUrl + "/verifier-email?token=" + token + "\n\n" +
                        "Ce lien est valable 24 heures.\n\n" +
                        "Si vous n'êtes pas à l'origine de cette inscription, ignorez ce message.\n\n" +
                        "L'équipe KANDIOU'S Fashion"
        );
        mailSender.send(message);
    }

    public void notifierAdminNouveauClient(String emailAdmin, String nomClient) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailExpediteur);
        message.setTo(emailAdmin);
        message.setSubject("Nouveau client inscrit - KANDIOU'S Fashion");
        message.setText(
                "Bonjour,\n\n" +
                        "Une nouvelle cliente vient de s'inscrire sur la boutique en ligne :\n\n" +
                        nomClient + "\n\n" +
                        "L'équipe KANDIOU'S Fashion"
        );
        mailSender.send(message);
    }
}