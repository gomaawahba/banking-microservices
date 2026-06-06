package com.banking.transaction.config;

import com.banking.transaction.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Value("${banking.kafka.topics.transactions:banking.transactions}")
    private String transactionsTopic;

    public void publishEvent(TransactionEvent event) {

        if (event == null) {
            log.error("Cannot publish null event");
            return;
        }

        String key = event.getTransactionId() != null
                ? event.getTransactionId().toString()
                : "UNKNOWN";

        kafkaTemplate.send(transactionsTopic, key, event)
                .whenComplete((result, ex) -> {

                    if (ex == null) {
                        log.info(
                                "Transaction event published successfully | type={} | txId={} | partition={}",
                                event.getEventType(),
                                key,
                                result.getRecordMetadata().partition()
                        );
                    } else {
                        log.error(
                                "Failed to publish transaction event | txId={}",
                                key,
                                ex
                        );
                    }
                });
    }
}