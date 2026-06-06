package com.banking.transaction.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {

    @NotBlank(message = "حساب المستلم مطلوب")
    private String toAccountId;

    @NotNull(message = "المبلغ مطلوب")
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Size(max = 500)
    private String description;

    private String currency;
}