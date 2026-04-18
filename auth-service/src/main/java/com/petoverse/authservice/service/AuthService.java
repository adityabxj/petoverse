package com.petoverse.authservice.service;

import com.petoverse.authservice.dto.LoginRequest;
import com.petoverse.authservice.dto.LoginResponse;
import com.petoverse.authservice.dto.RegisterRequest;
import com.petoverse.authservice.dto.RegisterResponse;
import com.petoverse.authservice.entity.User;
import com.petoverse.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getEmail());
                    return new IllegalArgumentException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("Login successful for user: {}", request.getEmail());

        return new LoginResponse(
                "Login successful",
                user.getRole()
        );
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return new RegisterResponse("User registered successfully", user.getEmail());
    }
}
