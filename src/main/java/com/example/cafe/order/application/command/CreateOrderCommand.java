package com.example.cafe.order.application.command;

import java.util.List;

public record CreateOrderCommand(
        Long userId,
        List<CreateOrderItemCommand> items
) {
}
