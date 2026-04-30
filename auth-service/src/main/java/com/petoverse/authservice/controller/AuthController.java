package com.petoverse.authservice.controller;

import com.petoverse.authservice.dto.LoginRequest;
import com.petoverse.authservice.dto.LoginResponse;
import com.petoverse.authservice.dto.RegisterRequest;
import com.petoverse.authservice.dto.RegisterResponse;
import com.petoverse.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public Map<String, String> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        authService.logout(authorizationHeader);
        return Map.of("message", "Logged out successfully");
    }
}
