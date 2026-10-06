package com.example.cafe.order.model;

import com.example.cafe.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "price", nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private OrderItem(Long menuId, Order order, int quantity, int price) {
        this.menuId = menuId;
        this.quantity = quantity;
        this.price = price;
        this.order = order;
    }

    public static OrderItem create(Long menuId, Order order, int quantity, int price) {
        return new OrderItem(menuId, order, quantity, price);
    }
}
