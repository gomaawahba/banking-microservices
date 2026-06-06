package com.banking.transaction.controller;

import com.banking.transaction.dto.DepositRequest;
import com.banking.transaction.dto.TransactionResponse;
import com.banking.transaction.dto.TransferRequest;
import com.banking.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String email
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        transactionService.transfer(
                                request,
                                UUID.fromString(userId),
                                email
                        )
                );
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String email
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        transactionService.deposit(
                                request,
                                UUID.fromString(userId),
                                email
                        )
                );
    }

    @GetMapping("/history")
    public ResponseEntity<Page<TransactionResponse>> getHistory(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        return ResponseEntity.ok(
                transactionService.getUserTransactions(
                        UUID.fromString(userId),
                        page,
                        size
                )
        );
    }

    @GetMapping("/{referenceNumber}")
    public ResponseEntity<TransactionResponse> getByReference(
            @PathVariable String referenceNumber
    ) {

        return ResponseEntity.ok(
                transactionService.getByReference(referenceNumber)
        );
    }
}