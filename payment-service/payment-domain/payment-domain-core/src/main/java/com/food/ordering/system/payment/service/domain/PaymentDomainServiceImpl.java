package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.domain.vo.Money;
import com.food.ordering.system.domain.vo.PaymentStatus;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.vo.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.vo.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.ZONE_ID;

@Slf4j
class PaymentDomainServiceImpl implements PaymentDomainService{

    @Override
    public PaymentEvent validateAndInitializePayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher
    ) {

        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Payment is initiated for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(
                    payment, ZonedDateTime.now(ZoneId.of(ZONE_ID)), paymentCompletedEventPublisher);
        } else {
            log.info("Payment initiation is failed for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(
                    payment, ZonedDateTime.now(ZoneId.of(ZONE_ID)), failureMessages, paymentFailedEventPublisher);
        }
    }

    @Override
    public PaymentEvent validateAndCancelPayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventPublisher
    ) {

        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment is cancelled for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(
                    payment, ZonedDateTime.now(ZoneId.of(ZONE_ID)), paymentCancelledEventPublisher);
        } else {
            log.info("Payment cancellation is failed for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(
                    payment, ZonedDateTime.now(ZoneId.of(ZONE_ID)), failureMessages, paymentFailedEventPublisher);
        }
    }

    private void validateCreditEntry(
            Payment payment,
            CreditEntry creditEntry,
            List<String> failureMessages
    ) {
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            log.error("Customer with id: {} does not have enough credit for payment!",
                    payment.getCustomerId().getValue());

            failureMessages.add("Customer with id=" + payment.getCustomerId().getValue()
                    + " does not have enough credit for payment");
        }
    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }

    private void updateCreditHistory(
            Payment payment,
            List<CreditHistory> creditHistories,
            TransactionType transactionType
    ) {
        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .amount(payment.getPrice())
                .transactionType(transactionType)
                .build());
    }

    private void validateCreditHistory(
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages
    ) {
        Money totalCreditHistoryAmount = getTotalHistoryAmount(creditHistories, TransactionType.CREDIT);
        Money totalDebitHistoryAmount = getTotalHistoryAmount(creditHistories, TransactionType.DEBIT);

        Money recordedTotalCreditAmount = totalCreditHistoryAmount.subtract(totalDebitHistoryAmount);
        if (!recordedTotalCreditAmount.isGreaterEqualThanZero()) {
            log.error("Customer with id: {} doesn't have enough credit according to credit history",
                    creditEntry.getCustomerId().getValue());

            failureMessages.add("Customer with id=" + creditEntry.getCustomerId().getValue()
                    + " doesn't have enough credit according to credit history");
        }

        if (!creditEntry.getTotalCreditAmount().equals(recordedTotalCreditAmount)) {
            log.error("Recorded total credit amount is not equal to current credit for customer id: {}!",
                    creditEntry.getCustomerId().getValue());

            failureMessages.add("Recorded total credit amount is not equal to current credit for customer id: " +
                    creditEntry.getCustomerId().getValue() + "!");
        }
    }

    private static Money getTotalHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {
        return creditHistories.stream()
                .filter(creditHistory -> transactionType == creditHistory.getTransactionType())
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

}
