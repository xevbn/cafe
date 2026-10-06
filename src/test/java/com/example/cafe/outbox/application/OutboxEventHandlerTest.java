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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxEventHandlerTest {
    @Mock
    private OutboxRepository outboxRepository;
    @Mock
    private OutboxService outboxService;
    @Mock
    private OutboxEventProducer outboxEventProducer;
    @InjectMocks
    private OutboxEventHandler outboxEventHandler;

    @Test
    @DisplayName("내부 이벤트를 감지해 kafka에 이벤트 발행")
    void 이벤트_발행() {
        //given
        OutboxEvent outboxEvent = OutboxEvent.create(
                1L,
                Map.of(),
                "event"
        );

        given(outboxEventProducer.send(any(OutboxEvent.class)))
                .willReturn(true);

        //when
        outboxEventHandler.publishEvent(outboxEvent);

        //then
        verify(outboxEventProducer).send(any(OutboxEvent.class));
        verify(outboxService).updateToProcessed(any(OutboxEvent.class));
    }

    @Test
    @DisplayName("처리되지 않은 이벤트 재발행")
    void 처리되지_않은_이벤트_스케줄러로_재발행() {
        //given
        OutboxEvent outboxEvent = OutboxEvent.create(
                1L,
                Map.of(),
                "event"
        );

        given(outboxRepository.findAllByStatusIsPending())
                .willReturn(List.of(outboxEvent));
        given(outboxEventProducer.send(any(OutboxEvent.class)))
            .willReturn(true);

        //when
        outboxEventHandler.republish();

        //then
        verify(outboxEventProducer).send(any(OutboxEvent.class));
        assertEquals(OutboxEventStatus.PROCESSED, outboxEvent.getStatus());
    }
}