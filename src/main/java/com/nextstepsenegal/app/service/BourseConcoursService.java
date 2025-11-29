package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.domain.BourseConcours;
import com.nextstepsenegal.app.repository.BourseConcoursRepository;
import com.nextstepsenegal.app.service.dto.BourseConcoursDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BourseConcoursService {

    private final BourseConcoursRepository repository;

    public BourseConcoursService(BourseConcoursRepository repository) {
        this.repository = repository;
    }

    // CREATE
    @Transactional
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

    // READ ALL
    @Transactional(readOnly = true)
    public List<BourseConcours> findAll() {
        List<BourseConcours> list = repository.findAll();

        list.forEach(this::safeInit);

        return list;
    }

    // READ ONE
    @Transactional(readOnly = true)
    public Optional<BourseConcours> findOne(Long id) {
        return repository
            .findById(id)
            .map(bc -> {
                safeInit(bc);
                return bc;
            });
    }

    // UPDATE
    @Transactional
    public Optional<BourseConcours> update(Long id, BourseConcoursDTO dto) {
        return repository
            .findById(id)
            .map(bc -> {
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

    // DELETE
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Force les listes à ne jamais être null
    private void safeInit(BourseConcours bc) {
        if (bc.getCriteres() == null) {
            bc.setCriteres(new ArrayList<>());
        } else {
            bc.getCriteres().size(); // initialise le proxy (sécurité)
        }
    }
}
