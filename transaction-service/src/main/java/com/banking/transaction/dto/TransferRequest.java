package com.banking.transaction.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotBlank(message = "حساب المُرسِل مطلوب")
    private String fromAccountId;

    @NotBlank(message = "حساب المُستقبِل مطلوب")
    private String toAccountId;

    @NotNull(message = "المبلغ مطلوب")
    @DecimalMin(value = "0.01", message = "المبلغ يجب أن يكون أكبر من صفر")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amount;

    @Size(max = 500)
    private String description;

    private String currency;
}