package com.example.cafe.outbox.application;

import com.example.cafe.outbox.model.OutboxEvent;
import com.example.cafe.outbox.model.OutboxEventStatus;
import com.example.cafe.outbox.model.OutboxRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private OutboxRepository repository;
    @InjectMocks
    private OutboxService outboxService;

    @Test
    @DisplayName("outboxEvent를 저장 및 발행한다")
    void outboxEvent_저장() {
        //given
        Map<String, Object> payload = Map.of(
                "payload", "payload"
        );

        given(repository.save(any()))
                .willReturn(OutboxEvent.create(1L, payload, "event"));

        //when
        outboxService.saveOutbox(1L, payload, "event");

        //then
        verify(repository).save(any(OutboxEvent.class));
        verify(publisher).publishEvent(any(OutboxEvent.class));
    }

    @Test
    @DisplayName("상태를 PROCESSED로 변경한다")
    void outboxEvent_상태를_PORCESSED로_변경한다() {
        //given
        OutboxEvent event = OutboxEvent.create(1L, Map.of(), "event");

        //when
        outboxService.updateToProcessed(event);

        //then
        assertEquals(OutboxEventStatus.PROCESSED, event.getStatus());
    }
}