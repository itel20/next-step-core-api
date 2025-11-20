package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.repository.*;
import com.nextstepsenegal.app.service.dto.StatsReponse;
import com.nextstepsenegal.app.service.dto.StatsReponses;
import org.springframework.stereotype.Service;

@Service
public class GestionService {

    private final EtudiantRepository etudiantRepository;
    private final EleveRepository eleveRepository;
    private final ConseillerRepository conseillerRepository;
    private final EtablissementRepository etablissementRepository;
    private final BourseConcoursRepository bourseConcoursRepository;
    private final FiliereRepository filiereRepository;

    public GestionService(
        EtudiantRepository etudiantRepository,
        EleveRepository eleveRepository,
        ConseillerRepository conseillerRepository,
        EtablissementRepository etablissementRepository,
        BourseConcoursRepository bourseConcoursRepository,
        FiliereRepository filiereRepository
    ) {
        this.etudiantRepository = etudiantRepository;
        this.eleveRepository = eleveRepository;
        this.conseillerRepository = conseillerRepository;
        this.etablissementRepository = etablissementRepository;
        this.bourseConcoursRepository = bourseConcoursRepository;
        this.filiereRepository = filiereRepository;
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

    public long countEtablissements() {
        return etablissementRepository.count();
    }

    public long countBourceConcours() {
        return bourseConcoursRepository.count();
    }

    public long countFiliere() {
        return filiereRepository.count();
    }

    public StatsReponses getAllStatistics() {
        StatsReponses stats = new StatsReponses();
        stats.setTotalBourceConcours(countBourceConcours());
        stats.setTotalFiliere(countFiliere());
        stats.setTotalEtablissement(countEtablissements());
        return stats;
    }
}
