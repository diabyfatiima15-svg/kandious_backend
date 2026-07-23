package com.kandious.controller;

import com.kandious.dto.RapportDTO;
import com.kandious.service.RapportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/rapports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    @GetMapping
    public ResponseEntity<RapportDTO> genererRapport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        RapportDTO rapport = rapportService.genererRapport(debut, fin);
        return ResponseEntity.ok(rapport);
    }
}