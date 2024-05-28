package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.domain.vo.PaymentOrderStatus;
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {
    public PaymentResponseAvroModel paymentCompletedEventToPaymentResponseAvroModel(
            PaymentCompletedEvent paymentCompletedEvent) {
        return paymentEventToPaymentResponseAvroModel(paymentCompletedEvent);
    }

    public PaymentResponseAvroModel paymentCancelledEventToPaymentResponseAvroModel(
            PaymentCancelledEvent paymentCancelledEvent) {
        return paymentEventToPaymentResponseAvroModel(paymentCancelledEvent);
    }

    public PaymentResponseAvroModel paymentFailedEventToPaymentResponseAvroModel(
            PaymentFailedEvent paymentFailedEvent) {
        return paymentEventToPaymentResponseAvroModel(paymentFailedEvent);
    }

    private PaymentResponseAvroModel paymentEventToPaymentResponseAvroModel(
            PaymentEvent paymentEvent) {

        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setPaymentId(paymentEvent.getPayment().getId().getValue().toString())
                .setCustomerId(paymentEvent.getPayment().getCustomerId().getValue().toString())
                .setOrderId(paymentEvent.getPayment().getOrderId().getValue().toString())
                .setPrice(paymentEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentEvent.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentEvent.getFailureMessages())
                .build();
    }

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(
            PaymentRequestAvroModel paymentRequestAvroModel) {

        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .customerId(paymentRequestAvroModel.getCustomerId())
                .orderId(paymentRequestAvroModel.getOrderId())
                .price(paymentRequestAvroModel.getPrice())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .build();
    }

}
