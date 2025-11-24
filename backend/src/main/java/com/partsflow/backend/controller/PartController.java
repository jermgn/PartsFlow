package com.partsflow.backend.controller;

import com.partsflow.backend.dto.part.PartCreateDTO;
import com.partsflow.backend.dto.part.PartResponseDTO;
import com.partsflow.backend.dto.part.PartUpdateDTO;
import com.partsflow.backend.service.PartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@CrossOrigin(origins = "*")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public List<PartResponseDTO> getAll() {
        return partService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponseDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(partService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PartCreateDTO dto) {
        try {
            PartResponseDTO saved = partService.create(dto);
            return ResponseEntity.created(URI.create("/api/parts/" + saved.id()))
                    .body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PartUpdateDTO dto) {
        try {
            return ResponseEntity.ok(partService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            partService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
