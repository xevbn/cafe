package com.example.cafe.outbox.infra;

import com.example.cafe.TestcontainersConfiguration;
import com.example.cafe.outbox.consumer.TestKafkaConsumerConfig;
import com.example.cafe.outbox.model.OutboxEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import({
        TestcontainersConfiguration.class,
        TestKafkaConsumerConfig.class
})
@Testcontainers
@ActiveProfiles("test")
class OutboxEventProducerTest {
    @Autowired
    private OutboxEventProducer producer;
    @Autowired
    private ConsumerFactory<String, OutboxEvent> consumerFactory;

    private Consumer<String, OutboxEvent> consumer;

    @Container
    static KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeEach
    void setUp() {
        consumer = consumerFactory.createConsumer();

        consumer.subscribe(List.of("outbox-event"));
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @DisplayName("kafka 메시지 발행")
    void send() {
        //given
        OutboxEvent event = OutboxEvent.create(
                1L,
                Map.of(),
                "event"
        );

        //when
        producer.send(event);

        //then
        ConsumerRecord<String, OutboxEvent> record = pollMessages();

        assertEquals(1L, record.value().getAggregateId());
        assertEquals("event", record.value().getEventType());
    }

    private ConsumerRecord<String, OutboxEvent> pollMessages() {
        ConsumerRecords<String, OutboxEvent> records;

        do {
            records = consumer.poll(Duration.ofMillis(100));
        } while (records.isEmpty());

        return records.iterator().next();
    }
}