package com.example.cafe.inventory.model;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    @DisplayName("재고량이 차감 수보다 크거나 같으면 재고량을 차감할 수 있다")
    void 재고량이_차감량보다_크거나_같으면_재고량을_차감한다() {
        //given
        Inventory inventory = Inventory.create(1L, 1);

        //when
        inventory.decreaseInventory(1);

        //then
        assertEquals(0, inventory.getStock());
    }

    @Test
    @DisplayName("재고량이 차감량보다 작으면 예외를 발생한다")
    void 재고량이_차감량보다_작으면_예외를_발생한다() {
        //given
        Inventory inventory = Inventory.create(1L, 1);

        //when&then
        assertThatThrownBy(() -> inventory.decreaseInventory(2))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }
}