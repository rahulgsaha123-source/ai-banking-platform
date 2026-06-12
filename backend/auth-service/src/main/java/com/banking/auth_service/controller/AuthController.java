package com.banking.auth_service.controller;

import com.banking.auth_service.dto.LoginRequest;
import com.banking.auth_service.dto.RegisterRequest;
import com.banking.auth_service.entity.Role;
import com.banking.auth_service.entity.User;
import com.banking.auth_service.repository.UserRepository;
import com.banking.auth_service.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // 1. Add JwtUtil

    // 2. Inject JwtUtil via constructor
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) 
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully!");
    }

    // 3. Add the Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        // Step A: Find the user by email
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid email or password!");
        }

        User user = optionalUser.get();

        // Step B: Verify the password
        // .matches() compares the raw text password to the hashed database password securely
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid email or password!");
        }

        // Step C: Generate the JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Step D: Return the token in a JSON response
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/test-security")
    public ResponseEntity<String> testSecurity() {
        return ResponseEntity.ok("You have successfully bypassed the bouncer using your JWT! Welcome to the secure zone.");
    }
}