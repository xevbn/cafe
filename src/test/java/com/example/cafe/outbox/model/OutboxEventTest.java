package com.example.cafe.outbox.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OutboxEventTest {

    @Test
    @DisplayName("outboxEvent 상태 변경")
    void outboxEvent_상태_변경() {
        //given
        OutboxEvent outboxEvent = OutboxEvent.create(
                1L,
                Map.of("payload", "payload"),
                "event"
        );

        //when
        outboxEvent.transitTo(OutboxEventStatus.PROCESSED);

        //then
        assertEquals(OutboxEventStatus.PROCESSED, outboxEvent.getStatus());
    }

    @Test
    @DisplayName("outboxEvent 재시도 횟수 증가")
    void outboxEvent_재시도_횟수_증가() {
        //given
        OutboxEvent outboxEvent = OutboxEvent.create(
                1L,
                Map.of("payload", "payload"),
                "event"
        );

        //when
        outboxEvent.increaseRetryCount();

        //then
        assertEquals(1, outboxEvent.getRetryCount());
    }

    @Test
    @DisplayName("재시도 횟수가 3회 초과 시 FAIL 처리")
    void 재시도_횟수_3회_초과() {
        //given
        OutboxEvent outboxEvent = OutboxEvent.create(
                1L,
                Map.of("payload", "payload"),
                "event"
        );

        //when
        for (int i = 0; i < 4; i++) {
            outboxEvent.increaseRetryCount();
        }

        //then
        assertEquals(4, outboxEvent.getRetryCount());
        assertEquals(OutboxEventStatus.FAILED, outboxEvent.getStatus());
    }
}