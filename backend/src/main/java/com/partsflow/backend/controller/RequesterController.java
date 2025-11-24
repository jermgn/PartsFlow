package com.partsflow.backend.controller;

import com.partsflow.backend.dto.requester.*;
import com.partsflow.backend.service.RequesterService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/requesters")
@CrossOrigin(origins = "*")
public class RequesterController {

    private final RequesterService requesterService;

    public RequesterController(RequesterService requesterService) {
        this.requesterService = requesterService;
    }

    @GetMapping
    public List<RequesterResponseDTO> getAll() {
        return requesterService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(requesterService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RequesterCreateDTO dto) {
        try {
            RequesterResponseDTO saved = requesterService.create(dto);
            return ResponseEntity.created(URI.create("/api/requesters/" + saved.id()))
                    .body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RequesterUpdateDTO dto) {
        try {
            return ResponseEntity.ok(requesterService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            requesterService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
