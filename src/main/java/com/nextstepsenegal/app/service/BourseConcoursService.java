package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.domain.BourseConcours;
import com.nextstepsenegal.app.repository.BourseConcoursRepository;
import com.nextstepsenegal.app.service.dto.BourseConcoursDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BourseConcoursService {

    private final BourseConcoursRepository repository;

    public BourseConcoursService(BourseConcoursRepository repository) {
        this.repository = repository;
    }

    // Create
    public BourseConcours create(BourseConcoursDTO dto) {
        BourseConcours bc = new BourseConcours();
        bc.setTitre(dto.getTitre());
        bc.setType(dto.getType());
        bc.setTypeBourse(dto.getTypeBourse());
        bc.setNombreBeneficiaires(dto.getNombreBeneficiaires());
        bc.setTauxAccepte(dto.getTauxAccepte());
        bc.setDateLimite(dto.getDateLimite());
        bc.setCriteres(dto.getCriteres());
        return repository.save(bc);
    }

    // Read all
    public List<BourseConcours> findAll() {
        return repository.findAll();
    }

    // Read by ID
    public Optional<BourseConcours> findOne(Long id) {
        return repository.findById(id);
    }

    // Update
    public Optional<BourseConcours> update(Long id, BourseConcoursDTO dto) {
        return repository.findById(id).map(bc -> {
            bc.setTitre(dto.getTitre());
            bc.setType(dto.getType());
            bc.setTypeBourse(dto.getTypeBourse());
            bc.setNombreBeneficiaires(dto.getNombreBeneficiaires());
            bc.setTauxAccepte(dto.getTauxAccepte());
            bc.setDateLimite(dto.getDateLimite());
            bc.setCriteres(dto.getCriteres());
            return repository.save(bc);
        });
    }

    // Delete
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
