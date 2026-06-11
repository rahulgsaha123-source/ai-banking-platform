package com.banking.auth_service.controller;

import com.banking.auth_service.dto.RegisterRequest;
import com.banking.auth_service.entity.Role;
import com.banking.auth_service.entity.User;
import com.banking.auth_service.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // 1. Check if the email already exists in the database
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // 2. Map the DTO to our User Entity
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword()) // Note: We will encrypt this tomorrow!
                .role(Role.CUSTOMER)
                .build();

        // 3. Save to PostgreSQL
        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }
}