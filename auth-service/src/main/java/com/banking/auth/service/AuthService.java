package com.banking.auth.service;

import com.banking.auth.dto.AuthDtos.*;
import com.banking.auth.dto.authDtosResponse;
import com.banking.auth.entity.RefreshToken;
import com.banking.auth.entity.Role;
import com.banking.auth.entity.User;
import com.banking.auth.exception.AuthException;
import com.banking.auth.repository.RefreshTokenRepository;
import com.banking.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // =========================
    // REGISTER
    // =========================

    public authDtosResponse.AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nationalId(request.getNationalId())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.CUSTOMER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    // =========================
    // LOGIN
    // =========================

    public authDtosResponse.AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildAuthResponse(user);
    }

    // =========================
    // REFRESH TOKEN
    // =========================

    public authDtosResponse.AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = refreshToken.getUser();

        if (refreshToken.isRevoked()
                || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(user);
    }

    // =========================
    // LOGOUT
    // =========================

    public void logout(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.revokeAllUserTokens(user.getId());
    }

    // =========================
    // VALIDATE TOKEN
    // =========================

    public authDtosResponse.ValidateTokenResponse validateToken(String token) {

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !jwtService.isTokenValid(token, user)) {
            return authDtosResponse.ValidateTokenResponse.builder()
                    .valid(false)
                    .message("Invalid token")
                    .build();
        }

        return authDtosResponse.ValidateTokenResponse.builder()
                .valid(true)
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId().toString())
                .build();
    }

    // =========================
    // PRIVATE
    // =========================

    private authDtosResponse.AuthResponse buildAuthResponse(User user) {

        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);

        return authDtosResponse.AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(authDtosResponse.UserInfo.from(user))
                .build();
    }
}