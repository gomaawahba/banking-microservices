package com.banking.transaction.dto;

import com.banking.transaction.entity.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private String id;
    private String referenceNumber;
    private String type;
    private String status;

    private String fromAccountId;
    private String toAccountId;

    private BigDecimal amount;
    private BigDecimal fee;
    private String currency;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public static TransactionResponse from(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId().toString())
                .referenceNumber(tx.getReferenceNumber())
                .type(tx.getType().name())
                .status(tx.getStatus().name())
                .fromAccountId(tx.getFromAccountId() != null ? tx.getFromAccountId().toString() : null)
                .toAccountId(tx.getToAccountId() != null ? tx.getToAccountId().toString() : null)
                .amount(tx.getAmount())
                .fee(tx.getFee())
                .currency(tx.getCurrency())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .processedAt(tx.getProcessedAt())
                .build();
    }
}