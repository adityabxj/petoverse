package com.petoverse.authservice.controller;

import com.petoverse.authservice.dto.LoginRequest;
import com.petoverse.authservice.dto.LoginResponse;
import com.petoverse.authservice.dto.RegisterRequest;
import com.petoverse.authservice.dto.RegisterResponse;
import com.petoverse.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
