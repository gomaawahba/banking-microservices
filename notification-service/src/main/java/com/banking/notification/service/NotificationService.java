package com.banking.notification.service;

import com.banking.notification.entity.Notification;
import com.banking.notification.enums.NotificationStatus;
import com.banking.notification.enums.NotificationType;
import com.banking.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    private static final String FROM_EMAIL = "gomaawahba53@gmail.com";

    @Async
    public void sendTransactionCompletedNotification(
            String userId,
            String email,
            String transactionType,
            BigDecimal amount,
            String currency,
            String referenceNumber
    ) {

        String subject = buildSubject(transactionType, true);

        String body = buildCompletedBody(
                transactionType,
                amount,
                currency,
                referenceNumber
        );

        Notification notification = Notification.builder()
                .userId(UUID.fromString(userId))
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .body(body)
                .recipient(email)
                .eventType("TRANSACTION_COMPLETED")
                .transactionRef(referenceNumber)
                .build();

        notification = notificationRepository.save(notification);

        sendEmail(notification);
    }

    @Async
    public void sendTransactionFailedNotification(
            String userId,
            String email,
            BigDecimal amount,
            String failureReason,
            String referenceNumber
    ) {

        String subject = "Transaction Failed";

        String body =
                "Dear Customer,\n\n" +
                        "Your transaction failed.\n\n" +
                        "Amount: " + amount + "\n" +
                        "Reference: " + referenceNumber + "\n" +
                        "Reason: " + failureReason + "\n";

        Notification notification = Notification.builder()
                .userId(UUID.fromString(userId))
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .body(body)
                .recipient(email)
                .eventType("TRANSACTION_FAILED")
                .transactionRef(referenceNumber)
                .build();

        notification = notificationRepository.save(notification);

        sendEmail(notification);
    }

    private void sendEmail(Notification notification) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject());
            message.setText(notification.getBody());
            message.setFrom(FROM_EMAIL);

            mailSender.send(message);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

            notificationRepository.save(notification);

            log.info("Email sent successfully to {}", notification.getRecipient());

        } catch (Exception e) {

            log.error("Email sending failed", e);

            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);

            notificationRepository.save(notification);
        }
    }

    private String buildSubject(String transactionType, boolean success) {

        return switch (transactionType) {
            case "TRANSFER" -> success ? "Transfer Completed" : "Transfer Failed";
            case "DEPOSIT" -> success ? "Deposit Completed" : "Deposit Failed";
            case "WITHDRAWAL" -> success ? "Withdrawal Completed" : "Withdrawal Failed";
            default -> success ? "Transaction Completed" : "Transaction Failed";
        };
    }

    private String buildCompletedBody(
            String type,
            BigDecimal amount,
            String currency,
            String ref
    ) {

        return "Dear Customer,\n\n" +
                "Your transaction was completed successfully.\n\n" +
                "Type: " + type + "\n" +
                "Amount: " + amount + " " + currency + "\n" +
                "Reference: " + ref + "\n" +
                "Date: " + LocalDateTime.now() + "\n\n" +
                "Thank you for banking with us.";
    }
}