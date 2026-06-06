package com.banking.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Auth Exceptions
 *
 * بدل ما نرجع generic exceptions، بنرجع exceptions واضحة
 * بتساعد الـ frontend يعرف إيه اللي حصل بالظبط.
 */
public class AuthException {

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) { super(message); }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class NationalIdAlreadyExistsException extends RuntimeException {
        public NationalIdAlreadyExistsException(String message) { super(message); }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) { super(message); }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) { super(message); }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AccountLockedException extends RuntimeException {
        public AccountLockedException(String message) { super(message); }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AccountDisabledException extends RuntimeException {
        public AccountDisabledException(String message) { super(message); }
    }
}