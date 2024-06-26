package com.food.ordering.system.order.service.application.rest;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders", produces = "application/vnd.API.v1+json")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    @PostMapping
    private ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody CreateOrderCommand createOrderCommand
    ) {
        log.info("Create order for customer: {} at restaurant: {}",
                createOrderCommand.getCustomerId(), createOrderCommand.getRestaurantId());
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        log.info("Order created with tracking id: {}", createOrderResponse.getOrderTrackingId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createOrderResponse);
    }

    @GetMapping("/{trackingId}")
    public TrackOrderResponse getOrderByTrackingId(@PathVariable UUID trackingId) {
        TrackOrderResponse trackOrderResponse = orderApplicationService.trackOrder(
                TrackOrderQuery.builder().orderTrackingId(trackingId).build()
        );
        log.info("Returning order status with tracking id: {}", trackOrderResponse.getOrderTrackingId());
        return trackOrderResponse;
    }
}
