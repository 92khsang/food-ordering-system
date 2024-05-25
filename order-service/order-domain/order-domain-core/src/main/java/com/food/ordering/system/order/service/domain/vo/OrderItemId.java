package com.food.ordering.system.order.service.domain.vo;

import com.food.ordering.system.domain.vo.BaseId;

// Only the aggregate root gets a unique ID(=UUID) because it is important
// Other entities use the ID as a Long type
public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long value) {
        super(value);
    }
}
