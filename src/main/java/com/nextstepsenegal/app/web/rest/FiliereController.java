package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.domain.Filiere;
import com.nextstepsenegal.app.service.FiliereService;
import com.nextstepsenegal.app.service.dto.FiliereDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/filieres")
public class FiliereController {

    private final FiliereService filiereService;

    public FiliereController(FiliereService filiereService) {
        this.filiereService = filiereService;
    }

    // ✔ CRÉATION
    @PostMapping
    public Filiere create(@RequestBody FiliereDTO dto) {
        return filiereService.create(dto);
    }
    // ✔ READ ALL
    @GetMapping
    public ResponseEntity<List<Filiere>> getAllFilieres() {
        List<Filiere> filieres = filiereService.findAll();
        return new ResponseEntity<>(filieres, HttpStatus.OK);
    }

    // ✔ READ ONE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Filiere> getFiliere(@PathVariable Long id) {
        Optional<Filiere> filiere = filiereService.findOne(id);
        return filiere.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✔ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Filiere> updateFiliere(@PathVariable Long id, @RequestBody FiliereDTO dto) {
        Optional<Filiere> updated = filiereService.update(id, dto);
        return updated.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✔ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiliere(@PathVariable Long id) {
        filiereService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
