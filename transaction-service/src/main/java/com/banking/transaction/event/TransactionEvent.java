package com.banking.transaction.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

    private String eventId;
    private String eventType;

    private String transactionId;
    private String referenceNumber;
    private String transactionType;
    private String status;

    private BigDecimal amount;
    private String currency;

    private String fromAccountId;
    private String toAccountId;

    private String userId;
    private String email;

    private String description;
    private String failureReason;

    private LocalDateTime timestamp;

    public static TransactionEvent completed(
            com.banking.transaction.entity.Transaction tx,
            String email
    ) {

        return TransactionEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("TRANSACTION_COMPLETED")
                .transactionId(tx.getId().toString())
                .referenceNumber(tx.getReferenceNumber())
                .transactionType(tx.getType().name())
                .status(tx.getStatus().name())
                .amount(tx.getAmount())
                .currency(tx.getCurrency())
                .fromAccountId(
                        tx.getFromAccountId() != null
                                ? tx.getFromAccountId().toString()
                                : null
                )
                .toAccountId(
                        tx.getToAccountId() != null
                                ? tx.getToAccountId().toString()
                                : null
                )
                .userId(tx.getInitiatedBy().toString())
                .email(email)
                .description(tx.getDescription())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static TransactionEvent failed(
            com.banking.transaction.entity.Transaction tx,
            String email
    ) {

        return TransactionEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("TRANSACTION_FAILED")
                .transactionId(tx.getId().toString())
                .referenceNumber(tx.getReferenceNumber())
                .transactionType(tx.getType().name())
                .status(tx.getStatus().name())
                .amount(tx.getAmount())
                .currency(tx.getCurrency())
                .userId(tx.getInitiatedBy().toString())
                .email(email)
                .failureReason(tx.getFailureReason())
                .timestamp(LocalDateTime.now())
                .build();
    }
}