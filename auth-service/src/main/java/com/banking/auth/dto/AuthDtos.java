package com.banking.auth.dto;


// ==========================================
// Request DTOs
// ==========================================

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthDtos {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "الاسم الكامل مطلوب")
        @Size(min = 3, max = 200, message = "الاسم بين 3 و200 حرف")
        private String fullName;

        @NotBlank(message = "البريد الإلكتروني مطلوب")
        @Email(message = "البريد الإلكتروني غير صحيح")
        private String email;

        @NotBlank(message = "كلمة المرور مطلوبة")
        @Size(min = 8, message = "كلمة المرور 8 أحرف على الأقل")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "كلمة المرور تحتاج: حرف كبير، حرف صغير، رقم، رمز خاص"
        )
        private String password;

        @NotBlank(message = "رقم الهوية مطلوب")
        @Size(min = 10, max = 20)
        private String nationalId;

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "رقم الهاتف غير صحيح")
        private String phoneNumber;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "البريد الإلكتروني مطلوب")
        @Email(message = "البريد الإلكتروني غير صحيح")
        private String email;

        @NotBlank(message = "كلمة المرور مطلوبة")
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {
        @NotBlank(message = "الـ Refresh Token مطلوب")
        private String refreshToken;
    }
}