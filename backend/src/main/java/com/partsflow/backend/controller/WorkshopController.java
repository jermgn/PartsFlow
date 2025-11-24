package com.partsflow.backend.controller;

import com.partsflow.backend.dto.workshop.WorkshopCreateDTO;
import com.partsflow.backend.dto.workshop.WorkshopResponseDTO;
import com.partsflow.backend.dto.workshop.WorkshopUpdateDTO;
import com.partsflow.backend.service.WorkshopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/workshops")
@CrossOrigin(origins = "*")
public class WorkshopController {

    private final WorkshopService workshopService;

    public WorkshopController(WorkshopService workshopService) {
        this.workshopService = workshopService;
    }

    @GetMapping
    public List<WorkshopResponseDTO> getAll() {
        return workshopService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(workshopService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WorkshopCreateDTO dto) {
        try {
            WorkshopResponseDTO saved = workshopService.create(dto);
            return ResponseEntity.created(URI.create("/api/workshops/" + saved.id()))
                    .body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody WorkshopUpdateDTO dto) {
        try {
            return ResponseEntity.ok(workshopService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            workshopService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
