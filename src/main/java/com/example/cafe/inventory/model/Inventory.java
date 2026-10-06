package com.example.cafe.inventory.model;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_id", unique = true, nullable = false)
    private Long menuId;

    private Integer stock;

    private Inventory(Long menuId, int stock) {
        this.menuId = menuId;
        this.stock = stock;
    }

    public static Inventory create(Long menuId, int stock) {
        return new Inventory(menuId, stock);
    }

    public void decreaseInventory(int amount) {
        if (this.stock < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }

        this.stock -= amount;
    }
}
