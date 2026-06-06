package com.banking.auth.controller;

import com.banking.auth.dto.AuthDtos.*;
import com.banking.auth.dto.UserInfoResponse;
import com.banking.auth.dto.authDtosResponse;
import com.banking.auth.entity.User;
import com.banking.auth.repository.UserRepository;
import com.banking.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<authDtosResponse.AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<authDtosResponse.AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    // REFRESH
    @PostMapping("/refresh")
    public ResponseEntity<authDtosResponse.AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    // GET USER BY ID (FIXED)


    @GetMapping("/users/{id}")
    public ResponseEntity<UserInfoResponse> getUserById(@PathVariable String id) {

        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID");
        }

        User user = userRepository.findById(uuid)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(
                new UserInfoResponse(
                        user.getId().toString(),
                        user.getEmail(),
                        user.getFullName()
                )
        );
    }
    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // VALIDATE TOKEN
    @GetMapping("/validate")
    public ResponseEntity<authDtosResponse.ValidateTokenResponse> validateToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        authDtosResponse.ValidateTokenResponse response =
                authService.validateToken(token);

        return ResponseEntity.status(
                response.isValid() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED
        ).body(response);
    }

    // ME
    @GetMapping("/me")
    public ResponseEntity<authDtosResponse.UserInfo> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return ResponseEntity.ok(
                authDtosResponse.UserInfo.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole().name())
                        .build()
        );
    }
}