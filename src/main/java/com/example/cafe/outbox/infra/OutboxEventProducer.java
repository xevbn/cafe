package com.example.cafe.outbox.infra;

import com.example.cafe.outbox.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventProducer implements com.example.cafe.outbox.application.OutboxEventProducer {
    private final KafkaTemplate<String, OutboxEvent> kafkaTemplate;
    private static final String TOPIC = "outbox-event";

    public boolean send(OutboxEvent event) {
        // 실패 시 재시도 로직 및 DLT 고민
        log.info("sending: {}", event);
        return kafkaTemplate.send(TOPIC, event).isDone();
    }
}
