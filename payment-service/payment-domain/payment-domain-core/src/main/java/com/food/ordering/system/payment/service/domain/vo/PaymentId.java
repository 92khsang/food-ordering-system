package com.food.ordering.system.payment.service.domain.vo;

import com.food.ordering.system.domain.vo.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
