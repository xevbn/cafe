package com.example.cafe.order.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("Order 애그리거트에서 OrderItem 생성까지 이뤄지는지 확인")
    void create() {
        //given
        List<OrderItemData> itemData = List.of(
                new OrderItemData(1L ,2, 1000),
                new OrderItemData(2L, 3, 1000)
        );

        //when
        Order order = Order.create(1L, itemData, 1000);

        //then
        assertNotNull(order.getOrderNum());
        assertNotNull(order.getOrderItems());
        assertEquals(2, order.getOrderItems().size());
        assertEquals(1L, order.getOrderItems().get(0).getMenuId());
        assertEquals(3, order.getOrderItems().get(1).getQuantity());
    }
}