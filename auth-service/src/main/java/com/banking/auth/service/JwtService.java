package com.banking.auth.service;

import com.banking.auth.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "QWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo5ODc2NTQzMjEwMTIzNDU2";

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // =========================
    // GENERATE
    // =========================

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());

        return buildToken(claims, user.getEmail(), accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return buildToken(claims, user.getEmail(), refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> claims, String subject, long exp) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(getSignKey())
                .compact();
    }

    // =========================
    // EXTRACT
    // =========================

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractEmail(String token) {
        return extractUsername(token);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // =========================
    // VALIDATION (OVERLOAD FIX)
    // =========================

    // for SERVICE layer (User)
    public boolean isTokenValid(String token, User user) {
        return extractUsername(token).equals(user.getEmail())
                && !isTokenExpired(token);
    }

    // for GATEWAY (String email) ✅ IMPORTANT FIX
    public boolean isTokenValid(String token, String email) {
        return extractUsername(token).equals(email)
                && !isTokenExpired(token);
    }

    // =========================
    // CLAIMS
    // =========================

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration / 1000;
    }
}