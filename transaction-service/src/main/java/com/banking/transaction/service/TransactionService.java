package com.banking.transaction.service;

import com.banking.transaction.config.TransactionEventProducer;
import com.banking.transaction.dto.DepositRequest;
import com.banking.transaction.dto.TransactionResponse;
import com.banking.transaction.dto.TransferRequest;
import com.banking.transaction.entity.Transaction;
import com.banking.transaction.entity.TransactionStatus;
import com.banking.transaction.entity.TransactionType;
import com.banking.transaction.event.TransactionEvent;
import com.banking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer eventProducer;

    @Value("${banking.transaction.daily-transfer-limit:50000.00}")
    private BigDecimal dailyTransferLimit;

    @Value("${banking.transaction.max-single-transfer:10000.00}")
    private BigDecimal maxSingleTransfer;

    // ==========================================
    // TRANSFER
    // ==========================================

    public TransactionResponse transfer(
            TransferRequest request,
            UUID userId,
            String email
    ) {

        UUID fromId = UUID.fromString(request.getFromAccountId());
        UUID toId = UUID.fromString(request.getToAccountId());

        log.info(
                "Transfer request: {} -> {} amount={}",
                fromId,
                toId,
                request.getAmount()
        );

        validateTransferLimits(
                fromId,
                request.getAmount()
        );

        Transaction transaction = Transaction.builder()
                .referenceNumber(generateReference())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .fromAccountId(fromId)
                .toAccountId(toId)
                .amount(request.getAmount())
                .currency(
                        request.getCurrency() != null
                                ? request.getCurrency()
                                : "SAR"
                )
                .description(request.getDescription())
                .initiatedBy(userId)
                .fee(
                        calculateFee(
                                request.getAmount(),
                                TransactionType.TRANSFER
                        )
                )
                .build();

        transaction = transactionRepository.save(transaction);

        try {

            processTransfer(transaction);

            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setProcessedAt(LocalDateTime.now());

            transaction = transactionRepository.save(transaction);

            eventProducer.publishEvent(
                    TransactionEvent.completed(
                            transaction,
                            email
                    )
            );

            log.info(
                    "Transfer completed: {}",
                    transaction.getReferenceNumber()
            );

        } catch (Exception ex) {

            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(ex.getMessage());

            transactionRepository.save(transaction);

            eventProducer.publishEvent(
                    TransactionEvent.failed(
                            transaction,
                            email
                    )
            );

            log.error(
                    "Transfer failed: {}",
                    transaction.getReferenceNumber(),
                    ex
            );

            throw ex;
        }

        return TransactionResponse.from(transaction);
    }

    // ==========================================
    // DEPOSIT
    // ==========================================

    public TransactionResponse deposit(
            DepositRequest request,
            UUID userId,
            String email
    ) {

        UUID toId = UUID.fromString(
                request.getToAccountId()
        );

        Transaction transaction = Transaction.builder()
                .referenceNumber(generateReference())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .toAccountId(toId)
                .amount(request.getAmount())
                .currency(
                        request.getCurrency() != null
                                ? request.getCurrency()
                                : "SAR"
                )
                .description(request.getDescription())
                .initiatedBy(userId)
                .fee(BigDecimal.ZERO)
                .build();

        transaction = transactionRepository.save(transaction);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setProcessedAt(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        eventProducer.publishEvent(
                TransactionEvent.completed(
                        transaction,
                        email
                )
        );

        log.info(
                "Deposit completed: {}",
                transaction.getReferenceNumber()
        );

        return TransactionResponse.from(transaction);
    }

    // ==========================================
    // HISTORY
    // ==========================================

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactions(
            UUID userId,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt")
                                .descending()
                );

        return transactionRepository
                .findByInitiatedByOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(TransactionResponse::from);
    }

    // ==========================================
    // GET BY REFERENCE
    // ==========================================

    @Transactional(readOnly = true)
    public TransactionResponse getByReference(
            String referenceNumber
    ) {

        return transactionRepository
                .findByReferenceNumber(referenceNumber)
                .map(TransactionResponse::from)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Transaction not found: "
                                        + referenceNumber
                        )
                );
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private void validateTransferLimits(
            UUID accountId,
            BigDecimal amount
    ) {

        if (amount.compareTo(maxSingleTransfer) > 0) {
            throw new RuntimeException(
                    "Maximum transfer exceeded: "
                            + maxSingleTransfer
            );
        }

        LocalDate today = LocalDate.now();

        BigDecimal dailyTotal =
                transactionRepository.sumDailyTransfers(
                        accountId,
                        today.atStartOfDay(),
                        today.plusDays(1).atStartOfDay()
                );

        if (dailyTotal == null) {
            dailyTotal = BigDecimal.ZERO;
        }

        if (dailyTotal.add(amount)
                .compareTo(dailyTransferLimit) > 0) {

            throw new RuntimeException(
                    "Daily transfer limit exceeded: "
                            + dailyTransferLimit
            );
        }
    }

    private void processTransfer(
            Transaction transaction
    ) {

        log.debug(
                "Processing transfer {}",
                transaction.getReferenceNumber()
        );

        // Account Service call later
    }

    private BigDecimal calculateFee(
            BigDecimal amount,
            TransactionType type
    ) {

        if (type == TransactionType.TRANSFER) {
            return amount.multiply(
                    new BigDecimal("0.005")
            );
        }

        return BigDecimal.ZERO;
    }

    private String generateReference() {

        return "TXN"
                + System.currentTimeMillis()
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
}