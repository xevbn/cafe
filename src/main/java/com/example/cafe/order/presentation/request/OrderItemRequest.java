package com.example.cafe.order.presentation.request;

import com.example.cafe.order.application.command.CreateOrderItemCommand;
import jakarta.validation.constraints.Min;

public record OrderItemRequest(
        Long menuId,
        @Min(1)
        Integer quantity,
        Integer price
) {
        public CreateOrderItemCommand toCreateOrderItemCommand() {
            return new CreateOrderItemCommand(menuId, quantity, price);
        }
}
