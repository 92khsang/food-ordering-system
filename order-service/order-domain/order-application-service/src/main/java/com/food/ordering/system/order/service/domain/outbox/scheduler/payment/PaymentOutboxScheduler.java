package com.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper paymentOutboxHelper;
    private final PaymentRequestMessagePublisher paymentRequestMessagePublisher;

    @Override
    @Transactional
    @Scheduled(
            fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}"
    )
    public void processOutboxMessage() {
        paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.STARTED,
                SagaStatus.STARTED, SagaStatus.COMPENSATING
        ).ifPresent(
                orderPaymentOutboxMessages -> {
                    log.info("Received {} OrderPaymentOutboxMessage with ids: {}, sending to message bus",
                            orderPaymentOutboxMessages.size(),
                            orderPaymentOutboxMessages.stream().map(outboxMessage ->
                                    outboxMessage.getId().toString()).collect(Collectors.joining(","))
                    );

                    orderPaymentOutboxMessages.forEach(orderPaymentOutboxMessage ->
                            paymentRequestMessagePublisher.publish(orderPaymentOutboxMessage, this::updateOutboxStatus));

                    log.info("{} OrderPaymentOutboxMessage sent to message bus!", orderPaymentOutboxMessages.size());
                }
        );
    }

    private void updateOutboxStatus(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
        orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
        paymentOutboxHelper.save(orderPaymentOutboxMessage);
        log.info("OrderPaymentOutboxMessage with id: {} updated with outbox status: {}",
                orderPaymentOutboxMessage.getId(), outboxStatus);
    }
}
