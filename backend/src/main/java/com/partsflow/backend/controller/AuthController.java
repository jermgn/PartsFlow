package com.partsflow.backend.controller;

import com.partsflow.backend.dto.auth.AuthRequestDTO;
import com.partsflow.backend.dto.auth.AuthResponseDTO;
import com.partsflow.backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO dto) {
        logger.info("[AUTH] Login attempt for username: {}", dto.username());

        try {
            AuthResponseDTO response = authService.login(dto);
            logger.info("[AUTH] Login successful for user: {}", dto.username());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("[AUTH] Login failed for user: {} -> {}", dto.username(), e.getMessage());
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("Invalid username or password"));
        }
    }

    record ErrorResponse(String message) {}
}
