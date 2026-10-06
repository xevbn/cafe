package com.example.cafe.outbox.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = OutboxEventStatus.PENDING")
    List<OutboxEvent> findAllByStatusIsPending();
}
