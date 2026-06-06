package com.banking.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Global Exception Handler
 *
 * بيمسك كل exceptions وبيرجع response موحّد ومنظّم.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(AuthException.EmailAlreadyExistsException e) {
        return buildError(HttpStatus.CONFLICT, "EMAIL_EXISTS", e.getMessage());
    }

    @ExceptionHandler(AuthException.NationalIdAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleNationalIdExists(AuthException.NationalIdAlreadyExistsException e) {
        return buildError(HttpStatus.CONFLICT, "NATIONAL_ID_EXISTS", e.getMessage());
    }

    @ExceptionHandler(AuthException.InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCreds(AuthException.InvalidCredentialsException e) {
        return buildError(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e.getMessage());
    }

    @ExceptionHandler(AuthException.InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(AuthException.InvalidTokenException e) {
        return buildError(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", e.getMessage());
    }

    @ExceptionHandler(AuthException.AccountLockedException.class)
    public ResponseEntity<ErrorResponse> handleAccountLocked(AuthException.AccountLockedException e) {
        return buildError(HttpStatus.FORBIDDEN, "ACCOUNT_LOCKED", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("بيانات غير صحيحة")
                .details(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Unexpected error: ", e);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "حدث خطأ غير متوقع. يرجى المحاولة مرة أخرى.");
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error(error)
                        .message(message)
                        .build()
        );
    }

    // ==========================================

    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> details;
    }
}