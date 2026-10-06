package com.example.cafe.order.application.command;

import com.example.cafe.order.model.OrderItemData;

public record CreateOrderItemCommand(
        Long menuId,
        Integer quantity,
        Integer price
) {
    public OrderItemData toOrderItemData() {
        return new OrderItemData(
                menuId,
                quantity,
                price
        );
    }
}
