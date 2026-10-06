package com.example.cafe.outbox.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Slf4j
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;
    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId;
    @Column(name = "event_type", nullable = false)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> payload;

    private OutboxEventStatus status;
    private int retryCount;

    private OutboxEvent(Long aggregateId, Map<String, Object> payload, String eventType) {
        this.eventId = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.retryCount = 0;
        this.status = OutboxEventStatus.PENDING;
    }

    public static OutboxEvent create(Long aggregateId, Map<String, Object> payload, String eventType) {
        return new OutboxEvent(
                aggregateId, payload, eventType
        );
    }

    public void transitTo(OutboxEventStatus nextStatus) {
        if (this.status.canTransitTo()) {
            this.status = nextStatus;
        }

        else {
            throw new IllegalStateException("유효하지 않은 상태 변경입니다.");
        }
    }

    public void increaseRetryCount() {
        this.retryCount++;

        if (this.retryCount > 3) {
            this.transitTo(OutboxEventStatus.FAILED);
            log.error("[주문 데이터 전송 실패]");
        }
    }
}
