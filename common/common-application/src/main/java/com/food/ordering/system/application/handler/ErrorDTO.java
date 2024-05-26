package com.food.ordering.system.application.handler;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class ErrorDTO {
    private final String code;
    private final String message;
}
