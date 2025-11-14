package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.domain.Etablissement;
import com.nextstepsenegal.app.service.EtablissementService;
import com.nextstepsenegal.app.service.dto.EtablissementDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/etablissements")
public class EtablissementController {

    private final EtablissementService service;

    public EtablissementController(EtablissementService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Etablissement> create(@RequestBody EtablissementDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Etablissement>> getAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Etablissement> getOne(@PathVariable Long id) {
        Optional<Etablissement> e = service.findOne(id);
        return e.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Etablissement> update(@PathVariable Long id, @RequestBody EtablissementDTO dto) {
        Optional<Etablissement> updated = service.update(id, dto);
        return updated.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
