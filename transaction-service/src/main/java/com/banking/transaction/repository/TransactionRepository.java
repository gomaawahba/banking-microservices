package com.banking.transaction.repository;

import com.banking.transaction.entity.Transaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByInitiatedByOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Transaction> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
            UUID fromAccountId, UUID toAccountId, Pageable pageable
    );

    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    // مجموع التحويلات اليومية لحساب معين
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.fromAccountId = :accountId
          AND t.type = 'TRANSFER'
          AND t.status = 'COMPLETED'
          AND t.createdAt >= :startOfDay
          AND t.createdAt < :endOfDay
    """)
    BigDecimal sumDailyTransfers(UUID accountId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}