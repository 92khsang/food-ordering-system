package com.food.ordering.system.restaurant.service.domain.vo;

import com.food.ordering.system.domain.vo.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
    public OrderApprovalId(UUID value) {
        super(value);
    }
}
