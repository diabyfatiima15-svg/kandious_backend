package com.kandious.service;

import com.kandious.dto.ClientInscriptionDTO;
import com.kandious.entity.Client;
import com.kandious.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientAuthService {

    private final ClientRepository clientRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String EMAIL_ADMIN = "diabyfatiima15@gmail.com";

    public void inscrire(ClientInscriptionDTO dto) {
        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé !");
        }

        Client client = new Client();
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setTelephone(dto.getTelephone());
        client.setAdresse(dto.getAdresse());
        client.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        client.setCompteEnLigne(true);
        client.setEmailVerifie(false);

        String token = UUID.randomUUID().toString();
        client.setTokenVerification(token);
        client.setDateInscription(LocalDateTime.now());

        clientRepository.save(client);

        emailService.envoyerEmailVerification(client.getEmail(), client.getNom(), token);
        emailService.notifierAdminNouveauClient(EMAIL_ADMIN, client.getNom() + " " + client.getPrenom());
    }

    public void verifierEmail(String token) {
        Client client = clientRepository.findByTokenVerification(token)
                .orElseThrow(() -> new RuntimeException("Lien de vérification invalide !"));

        client.setEmailVerifie(true);
        client.setTokenVerification(null);
        clientRepository.save(client);
    }
}