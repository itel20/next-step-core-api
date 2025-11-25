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

    private final FiliereRepository repository;

    public FiliereService(FiliereRepository repository) {
        this.repository = repository;
    }

    public Filiere create(FiliereDTO dto) {
        Filiere filiere = new Filiere();

        filiere.setTitre(dto.getTitre());
        filiere.setDomaine(dto.getDomaine());
        filiere.setDescriptionFormation(dto.getDescriptionFormation());
        filiere.setDifficulte(dto.getDifficulte());
        filiere.setTauxEmploi(dto.getTauxEmploi());
        filiere.setTauxSatisfaction(dto.getTauxSatisfaction());
        filiere.setSalaireMin(dto.getSalaireMin());
        filiere.setSalaireMax(dto.getSalaireMax());
        filiere.setDureeFormation(dto.getDureeFormation());
        filiere.setEcoles(dto.getEcoles());
        filiere.setDebouches(dto.getDebouches());
        filiere.setCompetences(dto.getCompetences());
        filiere.setTemoignages(dto.getTemoignages());

        return repository.save(filiere);
    }

    public List<Filiere> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Optional<Filiere> findOne(Long id) {
        return repository.findById(id);
    }

    public Optional<Filiere> update(Long id, FiliereDTO dto) {
        return repository.findById(id).map(filiere -> {
            filiere.setTitre(dto.getTitre());
            filiere.setDomaine(dto.getDomaine());
            filiere.setDescriptionFormation(dto.getDescriptionFormation());
            filiere.setDifficulte(dto.getDifficulte());
            filiere.setTauxEmploi(dto.getTauxEmploi());
            filiere.setTauxSatisfaction(dto.getTauxSatisfaction());
            filiere.setSalaireMin(dto.getSalaireMin());
            filiere.setSalaireMax(dto.getSalaireMax());
            filiere.setDureeFormation(dto.getDureeFormation());
            filiere.setEcoles(dto.getEcoles());
            filiere.setDebouches(dto.getDebouches());
            filiere.setCompetences(dto.getCompetences());
            filiere.setTemoignages(dto.getTemoignages());
            return repository.save(filiere);
        });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
