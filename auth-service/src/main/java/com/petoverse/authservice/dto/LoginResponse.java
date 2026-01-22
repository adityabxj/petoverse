package com.petoverse.authservice.dto;

public class LoginResponse {

    private String message;
    private String role;

    public LoginResponse(String message, String role) {
        this.message = message;
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }
}
