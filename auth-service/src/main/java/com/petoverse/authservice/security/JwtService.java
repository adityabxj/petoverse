package com.petoverse.authservice.security;

import com.petoverse.authservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final int MIN_SECRET_BYTES = 32;

    private final Key signingKey;
    private final long expirationSeconds;
    private final String issuer;
    private final String audience;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds:86400}") long expirationSeconds,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience) {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("JWT secret must be at least 256 bits");
        }

        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.expirationSeconds = expirationSeconds;
        this.issuer = issuer;
        this.audience = audience;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationSeconds);
        List<String> authorities = buildAuthorities(user.getRole());

        return Jwts.builder()
                .setClaims(Map.of(
                        "role", user.getRole(),
                        "authorities", authorities
                ))
                .setSubject(user.getEmail())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        Set<String> tokenAuthorities = extractAuthorities(claims);
        Set<String> userAuthorities = authorityNames(userDetails.getAuthorities());

        return userDetails.getUsername().equals(claims.getSubject())
                && userDetails.isEnabled()
                && issuer.equals(claims.getIssuer())
                && audience.equals(claims.getAudience())
                && tokenAuthorities.equals(userAuthorities)
                && claims.getExpiration().after(new Date());
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    public Instant extractExpiration(String token) {
        return extractAllClaims(token).getExpiration().toInstant();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private List<String> buildAuthorities(String role) {
        return List.of("ROLE_" + role);
    }

    private Set<String> extractAuthorities(Claims claims) {
        Object authoritiesClaim = claims.get("authorities");
        if (!(authoritiesClaim instanceof Collection<?> authorities)) {
            return Set.of();
        }

        return authorities.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toSet());
    }

    private Set<String> authorityNames(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
