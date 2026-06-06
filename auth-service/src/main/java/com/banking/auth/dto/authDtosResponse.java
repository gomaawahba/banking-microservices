package com.banking.auth.dto;

import com.banking.auth.entity.User;
import lombok.*;

public class authDtosResponse {

        // ==========================================
        // Response DTOs
        // ==========================================

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AuthResponse {
            private String accessToken;
            private String refreshToken;
            private String tokenType = "Bearer";
            private long expiresIn;   // بالثواني
            private UserInfo user;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UserInfo {
            private String id;
            private String email;
            private String fullName;
            private String role;

            public static UserInfo from(User user) {
                return UserInfo.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole().name())
                        .build();
            }
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ValidateTokenResponse {
            private boolean valid;
            private String userId;
            private String email;
            private String role;
            private String message;
        }
}

