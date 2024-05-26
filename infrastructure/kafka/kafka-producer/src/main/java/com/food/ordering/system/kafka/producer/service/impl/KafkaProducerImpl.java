package com.food.ordering.system.kafka.producer.service.impl;

import com.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    @Override
    public void send(String topicName, K key, V message, CompletableFuture<SendResult<K, V>> callback) {
        log.info("Sending message={} to topic={}", message, topicName);
        CompletableFuture<SendResult<K, V>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
        kafkaResultFuture.whenComplete((result, ex) -> {
            if (Objects.nonNull(ex)) {
                log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message, ex.getMessage());
                callback.completeExceptionally(new KafkaProducerException("Error on kafka producer with key: " + key + " and message: " + message));
            } else {
                callback.complete(result);
            }
        });
    }

    @PreDestroy
    public void close() {
        if (Objects.nonNull(kafkaTemplate)) {
            log.info("Closing kafka producer");
            // No explicit destroy method call here as Spring manages it.
            // However, in the lecture, implement it!
            kafkaTemplate.destroy();
        }
    }
}
