package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.service.EleveService;
import com.nextstepsenegal.app.service.EtudiantService;
import com.nextstepsenegal.app.service.GestionService;
import com.nextstepsenegal.app.service.dto.StatsReponse;
import com.nextstepsenegal.app.service.dto.StatsReponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatistiquesResource {

    private final GestionService statistiquesService;
    private final EleveService eleveService;
    private final EtudiantService etudiantService;

    public StatistiquesResource(GestionService statistiquesService, EleveService eleveService, EtudiantService etudiantService) {
        this.statistiquesService = statistiquesService;
        this.eleveService = eleveService;
        this.etudiantService = etudiantService;
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsReponse> getStats() {
        return ResponseEntity.ok(statistiquesService.getAllStats());
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatsReponses> getStatistics() {
        return ResponseEntity.ok(statistiquesService.getAllStatistics());
    }

    @GetMapping("/stats/serie")
    public ResponseEntity<Long> getStatsBySerie(@RequestParam String type, @RequestParam String serie) {
        long total;

        switch (type.toLowerCase()) {
            case "etudiant":
                total = etudiantService.countEtudiantsBySerie(serie);
                break;
            case "eleve":
                total = eleveService.countElevesBySerie(serie);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(total);
    }
}
