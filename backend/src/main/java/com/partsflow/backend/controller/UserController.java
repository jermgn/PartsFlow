package com.partsflow.backend.controller;

import com.partsflow.backend.dto.user.UserCreateDTO;
import com.partsflow.backend.dto.user.UserResponseDTO;
import com.partsflow.backend.dto.user.UserUpdateDTO;
import com.partsflow.backend.model.User;
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

    @GetMapping
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getUserRole(),
                        user.isEnabled()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getUserRole(),
                        user.isEnabled()
                ))
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

        if (dto.userRole() == null) {
            return ResponseEntity.badRequest().body("User role is required");
        }

        User user = User.builder()
                .username(dto.username())
                .passwordHash(dto.passwordHash())
                .email(dto.email())
                .userRole(dto.userRole())
                .enabled(true)
                .build();

        User saved = userRepository.save(user);

        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(new UserResponseDTO(
                        saved.getId(),
                        saved.getUsername(),
                        saved.getEmail(),
                        saved.getUserRole(),
                        saved.isEnabled()
                ));
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

                    return ResponseEntity.ok(new UserResponseDTO(
                            saved.getId(),
                            saved.getUsername(),
                            saved.getEmail(),
                            saved.getUserRole(),
                            saved.isEnabled()
                    ));
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
