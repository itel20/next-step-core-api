package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.domain.Filiere;
import com.nextstepsenegal.app.service.FiliereService;
import com.nextstepsenegal.app.service.dto.FiliereDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filieres")
public class FiliereController {

    private final FiliereService service;

    public FiliereController(FiliereService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Filiere> create(@RequestBody FiliereDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public List<Filiere> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Filiere> getOne(@PathVariable Long id) {
        return service.findOne(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Filiere> update(@PathVariable Long id, @RequestBody FiliereDTO dto) {
        return service.update(id, dto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
