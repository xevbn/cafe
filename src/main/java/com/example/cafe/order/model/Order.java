package com.example.cafe.order.model;

import com.example.cafe.common.entity.BaseEntity;
import com.example.cafe.order.presentation.request.OrderItemRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_num", unique = true, nullable = false)
    private UUID orderNum;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(Long userId, int totalPrice) {
        this.orderNum = UUID.randomUUID();
        this.userId = userId;
        this.status = OrderStatus.PAID;
        this.totalPrice = totalPrice;
    }

    public static Order create(Long userId, List<OrderItemData> items, int totalPrice) {
        Order order =  new Order(userId, totalPrice);

        for (OrderItemData item : items) {
            order.addItem(item);
        }

        return order;
    }

    private void addItem(OrderItemData item) {
        orderItems.add(OrderItem.create(item.menuId(), this, item.quantity(), item.price()));
    }
}
