package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.domain.Filiere;
import com.nextstepsenegal.app.repository.FiliereRepository;
import com.nextstepsenegal.app.service.dto.FiliereDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FiliereService {

    private final FiliereRepository repository;

    public FiliereService(FiliereRepository repository) {
        this.repository = repository;
    }

    // CREATE
    @Transactional
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

    // READ ALL
    @Transactional(readOnly = true)
    public List<Filiere> findAll() {
        List<Filiere> list = repository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        list.forEach(f -> {
            safeInit(f); // <- empêche LazyInitializationException
        });

        return list;
    }

    // READ ONE
    @Transactional(readOnly = true)
    public Optional<Filiere> findOne(Long id) {
        return repository
            .findById(id)
            .map(f -> {
                safeInit(f); // <- sécurise les listes
                return f;
            });
    }

    // UPDATE
    @Transactional
    public Optional<Filiere> update(Long id, FiliereDTO dto) {
        return repository
            .findById(id)
            .map(filiere -> {
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

    // DELETE
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // SECURE INITIALIZATION (empêche LazyInitializationException)
    private void safeInit(Filiere f) {
        if (f.getEcoles() == null) f.setEcoles(new ArrayList<>());
        else f.getEcoles().size();
        if (f.getDebouches() == null) f.setDebouches(new ArrayList<>());
        else f.getDebouches().size();
        if (f.getCompetences() == null) f.setCompetences(new ArrayList<>());
        else f.getCompetences().size();
    }
}
