package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.domain.Etablissement;
import com.nextstepsenegal.app.repository.EtablissementRepository;
import com.nextstepsenegal.app.service.dto.EtablissementDTO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EtablissementService {

    private final EtablissementRepository repository;

    public EtablissementService(EtablissementRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public Etablissement create(EtablissementDTO dto) {
        Etablissement e = new Etablissement();
        e.setNom(dto.getNom());
        e.setType(dto.getType());
        e.setClassement(dto.getClassement());
        e.setVille(dto.getVille());
        e.setPays(dto.getPays());
        e.setNombreEtudiants(dto.getNombreEtudiants());
        e.setNombreEnseignants(dto.getNombreEnseignants());
        e.setLaboratoiresBibliotheques(dto.getLaboratoiresBibliotheques());
        e.setFraisScolarite(dto.getFraisScolarite());
        e.setTauxSelectivite(dto.getTauxSelectivite());
        e.setFilieresDisponibles(dto.getFilieresDisponibles());
        e.setProcessusAdmission(dto.getProcessusAdmission());
        e.setTauxAcceptation(dto.getTauxAcceptation());
        e.setPointsBacRequis(dto.getPointsBacRequis());
        e.setVieEtudiante(dto.getVieEtudiante());
        e.setInformationsPratiques(dto.getInformationsPratiques());
        e.setTauxInsertion(dto.getTauxInsertion());
        e.setSalaireMoyen(dto.getSalaireMoyen());
        e.setTemoignages(dto.getTemoignages());
        return repository.save(e);
    }

    // READ ALL
    public List<Etablissement> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // READ ONE
    public Optional<Etablissement> findOne(Long id) {
        return repository.findById(id);
    }

    // UPDATE
    public Optional<Etablissement> update(Long id, EtablissementDTO dto) {
        return repository.findById(id).map(e -> {
            e.setNom(dto.getNom());
            e.setType(dto.getType());
            e.setClassement(dto.getClassement());
            e.setVille(dto.getVille());
            e.setPays(dto.getPays());
            e.setNombreEtudiants(dto.getNombreEtudiants());
            e.setNombreEnseignants(dto.getNombreEnseignants());
            e.setLaboratoiresBibliotheques(dto.getLaboratoiresBibliotheques());
            e.setFraisScolarite(dto.getFraisScolarite());
            e.setTauxSelectivite(dto.getTauxSelectivite());
            e.setFilieresDisponibles(dto.getFilieresDisponibles());
            e.setProcessusAdmission(dto.getProcessusAdmission());
            e.setTauxAcceptation(dto.getTauxAcceptation());
            e.setPointsBacRequis(dto.getPointsBacRequis());
            e.setVieEtudiante(dto.getVieEtudiante());
            e.setInformationsPratiques(dto.getInformationsPratiques());
            e.setTauxInsertion(dto.getTauxInsertion());
            e.setSalaireMoyen(dto.getSalaireMoyen());
            e.setTemoignages(dto.getTemoignages());
            return repository.save(e);
        });
    }

    // DELETE
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
