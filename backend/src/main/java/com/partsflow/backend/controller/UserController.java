package com.partsflow.backend.controller;

import com.partsflow.backend.model.User;
import com.partsflow.backend.model.UserRole;
import com.partsflow.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // DTO intégrés ici
    public record UserCreateDTO(
            String username,
            String passwordHash,
            String email,
            UserRole userRole
    ) {}

    public record UserUpdateDTO(
            String username,
            String email,
            UserRole userRole,
            Boolean enabled
    ) {}

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserCreateDTO dto) {

        if (dto.username() == null || dto.username().isBlank()) {
            return ResponseEntity.badRequest().body("Username is required");
        }

        if (userRepository.existsByUsername(dto.username())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userRepository.existsByEmail(dto.email())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = User.builder()
                .username(dto.username())
                .passwordHash(dto.passwordHash()) // pas de hashing pour MVP
                .email(dto.email())
                .userRole(dto.userRole())
                .enabled(true)
                .build();

        User saved = userRepository.save(user);

        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {

        return userRepository.findById(id)
                .map(existing -> {

                    if (dto.username() != null) {
                        existing.setUsername(dto.username());
                    }

                    if (dto.email() != null) {
                        existing.setEmail(dto.email());
                    }

                    if (dto.userRole() != null) {
                        existing.setUserRole(dto.userRole());
                    }

                    if (dto.enabled() != null) {
                        existing.setEnabled(dto.enabled());
                    }

                    User saved = userRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
