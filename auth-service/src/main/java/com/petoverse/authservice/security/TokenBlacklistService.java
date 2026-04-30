package com.petoverse.authservice.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, Instant expiresAt) {
        cleanupExpired();
        blacklistedTokens.put(token, expiresAt);
    }

    public boolean isBlacklisted(String token) {
        cleanupExpired();
        Instant expiresAt = blacklistedTokens.get(token);
        return expiresAt != null && expiresAt.isAfter(Instant.now());
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> !entry.getValue().isAfter(now));
    }
}
