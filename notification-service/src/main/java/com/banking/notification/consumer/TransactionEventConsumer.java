package com.banking.notification.consumer;

import com.banking.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "${banking.kafka.topics.transactions:banking.transactions}",
            groupId = "${spring.kafka.consumer.group-id:notification-group}"
    )
    public void handleTransactionEvent(
            ConsumerRecord<String, String> record
    ) {

        try {

            if (record.value() == null || record.value().isEmpty()) {
                log.warn("Empty event received");
                return;
            }

            Map<String, Object> event =
                    objectMapper.readValue(record.value(), Map.class);

            String eventType = (String) event.get("eventType");
            String email = (String) event.get("email");
            String userId = (String) event.get("userId");

            if (email == null || userId == null) {
                log.error("Email or UserId missing in event");
                return;
            }

            switch (eventType) {

                case "TRANSACTION_COMPLETED" ->

                        notificationService.sendTransactionCompletedNotification(
                                userId,
                                email,
                                (String) event.get("transactionType"),
                                new BigDecimal(event.get("amount").toString()),
                                (String) event.get("currency"),
                                (String) event.get("referenceNumber")
                        );

                case "TRANSACTION_FAILED" ->

                        notificationService.sendTransactionFailedNotification(
                                userId,
                                email,
                                new BigDecimal(event.get("amount").toString()),
                                (String) event.get("failureReason"),
                                (String) event.get("referenceNumber")
                        );

                default ->
                        log.warn("Unknown event type {}", eventType);
            }

        } catch (Exception e) {

            log.error("Kafka processing error", e);
        }
    }
}