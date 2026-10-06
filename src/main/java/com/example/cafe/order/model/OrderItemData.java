package com.example.cafe.order.model;

public record OrderItemData(
        Long menuId,
        int quantity,
        int price
) {
}
