package com.kandious.controller;

import com.kandious.entity.Utilisateur;
import com.kandious.repository.UtilisateurRepository;
import com.kandious.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getStatsAdmin() {
        return ResponseEntity.ok(
                dashboardService.getStatsAdmin());
    }

    @GetMapping("/vendeur")
    public ResponseEntity<Map<String, Object>> getStatsVendeur() {
        return ResponseEntity.ok(
                dashboardService.getStatsVendeur());
    }

    @GetMapping("/caissier")
    public ResponseEntity<Map<String, Object>> getStatsCaissier(
            Authentication authentication) {
        Long userId = utilisateurRepository
                .findByEmail(authentication.getName())
                .map(Utilisateur::getId)
                .orElse(null);
        return ResponseEntity.ok(
                dashboardService.getStatsCaissier(userId));
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getNotifications(
            Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .map(a -> a.replace("ROLE_", ""))
                .orElse("");
        return ResponseEntity.ok(
                dashboardService.getNotifications(role));
    }
}