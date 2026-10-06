package com.example.cafe.outbox.application;

import com.example.cafe.outbox.model.OutboxEvent;
import com.example.cafe.outbox.model.OutboxEventStatus;
import com.example.cafe.outbox.model.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxEventHandler {
    private final OutboxEventProducer outboxEventProducer;
    private final OutboxRepository outboxRepository;
    private final OutboxService outboxService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(OutboxEvent event) {
        if(outboxEventProducer.send(event)) {
            outboxService.updateToProcessed(event);
        }
    }

    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void republish() {
        List<OutboxEvent> events = outboxRepository.findAllByStatusIsPending();

        events.forEach(event -> {
            event.increaseRetryCount();

            if (outboxEventProducer.send(event)) {
                event.transitTo(OutboxEventStatus.PROCESSED);
            }
        });

        outboxRepository.saveAll(events);
    }
}
