package com.food.ordering.system.saga;

public enum SagaStatus {
    START, FAIL, SUCCEEDED, PROCESSING, COMPENSATING, COMPENSATED
}
