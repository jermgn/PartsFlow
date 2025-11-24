package com.partsflow.backend.controller;

import com.partsflow.backend.dto.modification.*;
import com.partsflow.backend.model.RequestStatus;
import com.partsflow.backend.service.ModificationRequestService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class ModificationRequestController {

    private final ModificationRequestService service;

    public ModificationRequestController(ModificationRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<ModificationRequestResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ModificationRequestCreateDTO dto) {
        try {
            ModificationRequestResponseDTO saved = service.create(dto);
            return ResponseEntity.created(URI.create("/api/requests/" + saved.id()))
                    .body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ModificationRequestUpdateDTO dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    public List<ModificationRequestResponseDTO> getByStatus(@PathVariable RequestStatus status) {
        return service.getByStatus(status);
    }

    @GetMapping("/workshop/{id}")
    public List<ModificationRequestResponseDTO> getByWorkshop(@PathVariable Long id) {
        return service.getByWorkshop(id);
    }
}
