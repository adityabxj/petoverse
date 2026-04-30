package com.petoverse.authservice.dto;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String role;

    public LoginResponse(String accessToken, String tokenType, long expiresIn, String role) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getRole() {
        return role;
    }
}
