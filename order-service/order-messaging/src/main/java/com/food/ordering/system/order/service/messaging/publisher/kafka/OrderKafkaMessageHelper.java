package com.food.ordering.system.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class OrderKafkaMessageHelper {

    public <T> CompletableFuture<SendResult<String, T>> getKafkaCallback(
            String responseTopicName,
            T requestAvroModel,
            String orderId,
            String requestAvroModelName
    ) {
        return new CompletableFuture<>() {

            @Override
            public boolean complete(SendResult<String, T> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for order id: {} " +
                                "Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp()
                );
                return super.complete(result);
            }

            @Override
            public boolean completeExceptionally(Throwable ex) {
                log.error("Error while sending " + requestAvroModelName +
                        " message {} to topic {}", requestAvroModel.toString(), responseTopicName, ex);
                return super.completeExceptionally(ex);
            }
        };
    }

}
