package com.example.cafe.outbox.application;

import com.example.cafe.outbox.model.OutboxEvent;
import com.example.cafe.outbox.model.OutboxEventStatus;
import com.example.cafe.outbox.model.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void saveOutbox(Long aggregateId, Map<String, Object> payload, String eventType) {
        OutboxEvent event = outboxRepository.save(OutboxEvent.create(aggregateId, payload, eventType));

        applicationEventPublisher.publishEvent(event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToProcessed(OutboxEvent outboxEvent) {
        outboxEvent.transitTo(OutboxEventStatus.PROCESSED);

        outboxRepository.save(outboxEvent);
    }
}
