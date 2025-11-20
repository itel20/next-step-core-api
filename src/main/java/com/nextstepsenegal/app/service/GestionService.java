package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.repository.ConseillerRepository;
import com.nextstepsenegal.app.repository.EleveRepository;
import com.nextstepsenegal.app.repository.EtudiantRepository;
import com.nextstepsenegal.app.service.dto.StatsReponse;
import org.springframework.stereotype.Service;

@Service
public class GestionService {

    private final EtudiantRepository etudiantRepository;
    private final EleveRepository eleveRepository;
    private final ConseillerRepository conseillerRepository;

    public GestionService(
        EtudiantRepository etudiantRepository,
        EleveRepository eleveRepository,
        ConseillerRepository conseillerRepository
    ) {
        this.etudiantRepository = etudiantRepository;
        this.eleveRepository = eleveRepository;
        this.conseillerRepository = conseillerRepository;
    }

    public long countEtudiants() {
        return etudiantRepository.count();
    }

    public long countEleves() {
        return eleveRepository.count();
    }

    public long countConseillers() {
        return conseillerRepository.count();
    }

    public StatsReponse getAllStats() {
        StatsReponse stats = new StatsReponse();
        stats.setTotalEtudiants(countEtudiants());
        stats.setTotalEleves(countEleves());
        stats.setTotalConseillers(countConseillers());
        stats.setTotalAll(countEtudiants() + countConseillers() + countEleves());
        return stats;
    }
}
