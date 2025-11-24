package com.partsflow.backend.service;

import com.partsflow.backend.dto.user.UserCreateDTO;
import com.partsflow.backend.dto.user.UserResponseDTO;
import com.partsflow.backend.dto.user.UserUpdateDTO;
import com.partsflow.backend.model.User;
import com.partsflow.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return toDTO(user);
    }

    public UserResponseDTO create(UserCreateDTO dto) {

        if (dto.username() == null || dto.username().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (dto.userRole() == null) {
            throw new IllegalArgumentException("User role is required");
        }

        User user = User.builder()
                .username(dto.username())
                .passwordHash(passwordEncoder.encode(dto.passwordHash()))
                .email(dto.email())
                .userRole(dto.userRole())
                .enabled(true)
                .build();

        userRepository.save(user);
        return toDTO(user);
    }

    public UserResponseDTO update(Long id, UserUpdateDTO dto) {

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.username() != null) existing.setUsername(dto.username());
        if (dto.email() != null) existing.setEmail(dto.email());
        if (dto.userRole() != null) existing.setUserRole(dto.userRole());
        if (dto.enabled() != null) existing.setEnabled(dto.enabled());

        userRepository.save(existing);

        return toDTO(existing);
    }

    public void delete(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole(),
                user.isEnabled()
        );
    }
}
