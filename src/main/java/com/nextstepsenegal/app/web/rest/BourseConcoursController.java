package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.domain.BourseConcours;
import com.nextstepsenegal.app.service.BourseConcoursService;
import com.nextstepsenegal.app.service.dto.BourseConcoursDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bourses")
public class BourseConcoursController {

    private final BourseConcoursService service;

    public BourseConcoursController(BourseConcoursService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BourseConcours> create(@RequestBody BourseConcoursDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<BourseConcours>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BourseConcours> getOne(@PathVariable Long id) {
        return service.findOne(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BourseConcours> update(@PathVariable Long id, @RequestBody BourseConcoursDTO dto) {
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
