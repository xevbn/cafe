package com.example.cafe.outbox.application;

import com.example.cafe.outbox.model.OutboxEvent;

public interface OutboxEventProducer {
    boolean send(OutboxEvent event);
}
