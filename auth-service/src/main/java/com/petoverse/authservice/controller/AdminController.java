package com.petoverse.authservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminHealth() {
        return Map.of("message", "Admin endpoint accessible");
    }
}
