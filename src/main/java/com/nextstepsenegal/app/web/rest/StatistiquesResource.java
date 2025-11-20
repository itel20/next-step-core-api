package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.service.GestionService;
import com.nextstepsenegal.app.service.dto.StatsReponse;
import com.nextstepsenegal.app.service.dto.StatsReponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatistiquesResource {

    private final GestionService statistiquesService;

    public StatistiquesResource(GestionService statistiquesService) {
        this.statistiquesService = statistiquesService;
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsReponse> getStats() {
        return ResponseEntity.ok(statistiquesService.getAllStats());
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatsReponses> getStatistics() {
        return ResponseEntity.ok(statistiquesService.getAllStatistics());
    }
}
