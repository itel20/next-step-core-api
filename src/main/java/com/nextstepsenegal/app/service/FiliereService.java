package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.domain.Filiere;
import com.nextstepsenegal.app.repository.FiliereRepository;
import com.nextstepsenegal.app.service.dto.FiliereDTO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FiliereService {

    private final FiliereRepository filiereRepository;

    public FiliereService(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    // ✔ MÉTHODE DE CRÉATION
    public Filiere create(FiliereDTO dto) {

        Filiere filiere = new Filiere();
        filiere.setTitre(dto.getTitre());
        filiere.setCategorie(dto.getCategorie());
        filiere.setDescriptionDetaillee(dto.getDescriptionDetaillee());
        filiere.setDifficulte(dto.getDifficulte());
        filiere.setTauxEmploi(dto.getTauxEmploi());
        filiere.setSatisfaction(dto.getSatisfaction());
        filiere.setSalaireMoyen(dto.getSalaireMoyen());
        filiere.setDureeFormation(dto.getDureeFormation());
        filiere.setUniversites(dto.getUniversites());
        filiere.setDebouches(dto.getDebouches());
        filiere.setCompetences(dto.getCompetences());
        filiere.setTemoignages(dto.getTemoignages());

        return filiereRepository.save(filiere);
    }
    // ✔ MÉTHODE POUR LIRE TOUTES LES FILIÈRES
    public List<Filiere> findAll() {
        return filiereRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // ✔ MÉTHODE POUR LIRE UNE FILIÈRE PAR ID
    public Optional<Filiere> findOne(Long id) {
        return filiereRepository.findById(id);
    }

    // ✔ MÉTHODE DE MISE À JOUR
    public Optional<Filiere> update(Long id, FiliereDTO dto) {
        return filiereRepository.findById(id).map(filiere -> {
            filiere.setTitre(dto.getTitre());
            filiere.setCategorie(dto.getCategorie());
            filiere.setDescriptionDetaillee(dto.getDescriptionDetaillee());
            filiere.setDifficulte(dto.getDifficulte());
            filiere.setTauxEmploi(dto.getTauxEmploi());
            filiere.setSatisfaction(dto.getSatisfaction());
            filiere.setSalaireMoyen(dto.getSalaireMoyen());
            filiere.setDureeFormation(dto.getDureeFormation());
            filiere.setUniversites(dto.getUniversites());
            filiere.setDebouches(dto.getDebouches());
            filiere.setCompetences(dto.getCompetences());
            filiere.setTemoignages(dto.getTemoignages());
            return filiereRepository.save(filiere);
        });
    }

    // ✔ MÉTHODE DE SUPPRESSION
    public void delete(Long id) {
        filiereRepository.deleteById(id);
    }
}
