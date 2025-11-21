package com.partsflow.backend.controller;

import com.partsflow.backend.dto.requester.*;
import com.partsflow.backend.model.Requester;
import com.partsflow.backend.model.User;
import com.partsflow.backend.repository.RequesterRepository;
import com.partsflow.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/requesters")
@CrossOrigin(origins = "*")
public class RequesterController {

    private final RequesterRepository requesterRepository;
    private final UserRepository userRepository;

    public RequesterController(RequesterRepository requesterRepository, UserRepository userRepository) {
        this.requesterRepository = requesterRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<RequesterResponseDTO> getAll() {
        return requesterRepository.findAll().stream()
                .map(r -> new RequesterResponseDTO(
                        r.getId(),
                        r.getFirstName(),
                        r.getLastName(),
                        r.getFullName(),
                        r.getDepartment(),
                        r.getSite(),
                        r.getBusinessRole(),
                        r.getUser().getId()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequesterResponseDTO> getById(@PathVariable Long id) {
        return requesterRepository.findById(id)
                .map(r -> new RequesterResponseDTO(
                        r.getId(),
                        r.getFirstName(),
                        r.getLastName(),
                        r.getFullName(),
                        r.getDepartment(),
                        r.getSite(),
                        r.getBusinessRole(),
                        r.getUser().getId()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RequesterCreateDTO dto) {

        User user = userRepository.findById(dto.userId())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (requesterRepository.existsByUser(user)) {
            return ResponseEntity.badRequest().body("Requester already exists for this user");
        }

        Requester requester = Requester.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .department(dto.department())
                .site(dto.site())
                .businessRole(dto.businessRole())
                .user(user)
                .build();

        Requester saved = requesterRepository.save(requester);

        return ResponseEntity.created(URI.create("/api/requesters/" + saved.getId()))
                .body(new RequesterResponseDTO(
                        saved.getId(),
                        saved.getFirstName(),
                        saved.getLastName(),
                        saved.getFullName(),
                        saved.getDepartment(),
                        saved.getSite(),
                        saved.getBusinessRole(),
                        saved.getUser().getId()
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RequesterUpdateDTO dto) {

        return requesterRepository.findById(id)
                .map(existing -> {

                    if (dto.firstName() != null) existing.setFirstName(dto.firstName());
                    if (dto.lastName() != null) existing.setLastName(dto.lastName());
                    if (dto.department() != null) existing.setDepartment(dto.department());
                    if (dto.site() != null) existing.setSite(dto.site());
                    if (dto.businessRole() != null) existing.setBusinessRole(dto.businessRole());

                    Requester saved = requesterRepository.save(existing);

                    return ResponseEntity.ok(
                            new RequesterResponseDTO(
                                    saved.getId(),
                                    saved.getFirstName(),
                                    saved.getLastName(),
                                    saved.getFullName(),
                                    saved.getDepartment(),
                                    saved.getSite(),
                                    saved.getBusinessRole(),
                                    saved.getUser().getId()
                            )
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        if (!requesterRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        requesterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
