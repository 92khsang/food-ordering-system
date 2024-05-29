package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.vo.OrderApprovalStatus;
import com.food.ordering.system.domain.vo.OrderId;
import com.food.ordering.system.domain.vo.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.vo.OrderApprovalId;

public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final OrderId orderId;
    private final RestaurantId restaurantId;
    private final OrderApprovalStatus approvalStatus;

    private OrderApproval(Builder builder) {
        super.setId(builder.orderApprovalId);
        orderId = builder.orderId;
        restaurantId = builder.restaurantId;
        approvalStatus = builder.approvalStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public OrderApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public static final class Builder {
        private OrderApprovalId orderApprovalId;
        private OrderId orderId;
        private RestaurantId restaurantId;
        private OrderApprovalStatus approvalStatus;

        private Builder() {
        }

        public Builder orderApprovalId(OrderApprovalId val) {
            orderApprovalId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder approvalStatus(OrderApprovalStatus val) {
            approvalStatus = val;
            return this;
        }

        public OrderApproval build() {
            return new OrderApproval(this);
        }
    }
}
