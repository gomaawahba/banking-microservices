package com.banking.gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> auth() {

        return build(
                "auth-service",
                "Auth Service unavailable"
        );
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> user() {

        return build(
                "user-service",
                "User Service unavailable"
        );
    }

    @GetMapping("/transaction")
    public ResponseEntity<Map<String, Object>> transaction() {

        return build(
                "transaction-service",
                "Transaction Service unavailable"
        );
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notification() {

        return build(
                "notification-service",
                "Notification Service unavailable"
        );
    }

    private ResponseEntity<Map<String, Object>> build(
            String service,
            String message
    ) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "service", service,
                                "status", 503,
                                "message", message
                        )
                );
    }
}