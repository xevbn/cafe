package com.example.cafe.order.presentation.request;

import com.example.cafe.order.application.command.CreateOrderCommand;

import java.util.List;

public record OrderCreateRequest(
        Long userId,
        List<OrderItemRequest> items
) {
    public CreateOrderCommand toCreateOrderCommand() {
        return new CreateOrderCommand(
                userId,
                items.stream()
                        .map(OrderItemRequest::toCreateOrderItemCommand)
                        .toList()
        );
    }
}

