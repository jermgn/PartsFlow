package com.partsflow.backend.controller;

import com.partsflow.backend.model.Requester;
import com.partsflow.backend.model.User;
import com.partsflow.backend.model.RequesterRole;
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

    public RequesterController(RequesterRepository requesterRepository,
                               UserRepository userRepository) {
        this.requesterRepository = requesterRepository;
        this.userRepository = userRepository;
    }

    public record RequesterCreateDTO(
            String firstName,
            String lastName,
            String department,
            String site,
            RequesterRole businessRole,
            Long userId
    ) {}

    public record RequesterUpdateDTO(
            String firstName,
            String lastName,
            String department,
            String site,
            RequesterRole businessRole
    ) {}

    @GetMapping
    public List<Requester> getAll() {
        return requesterRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Requester> getById(@PathVariable Long id) {
        return requesterRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RequesterCreateDTO dto) {

        User user = userRepository.findById(dto.userId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (requesterRepository.existsByUserId(dto.userId())) {
            return ResponseEntity.badRequest().body("This user already has a requester profile");
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
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RequesterUpdateDTO dto) {
        return requesterRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(dto.firstName());
                    existing.setLastName(dto.lastName());
                    existing.setDepartment(dto.department());
                    existing.setSite(dto.site());
                    existing.setBusinessRole(dto.businessRole());
                    Requester saved = requesterRepository.save(existing);
                    return ResponseEntity.ok(saved);
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
