package com.example.cafe.inventory.application;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.inventory.model.Inventory;
import com.example.cafe.inventory.model.InventoryRepository;
import com.example.cafe.menu.application.MenuService;
import com.example.cafe.menu.model.Menu;
import com.example.cafe.menu.model.MenuStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private MenuService menuService;
    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("재고 차감")
    void decreaseStock() {
        //given
        Menu menu = Menu.create("name", 1000);
        ReflectionTestUtils.setField(menu, "id", 1L);

        Inventory inventory = Inventory.create(menu.getId(), 10);

        given(inventoryRepository.findWithLockByMenuId(anyLong()))
                .willReturn(Optional.of(inventory));

        //when
        inventoryService.decreaseStock(menu.getId(), 2);

        //then
        assertEquals(8, inventory.getStock());
    }

    @Test
    @DisplayName("차감 수가 0이하면 예외를 반환한다")
    void 차감량이_0이하면_예외를_반환한다() {
        //given
        Menu menu = Menu.create("name", 1000);
        ReflectionTestUtils.setField(menu, "id", 1L);

        //when&then
        assertThatThrownBy(() -> inventoryService.decreaseStock(menu.getId(), 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_QUANTITY_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("재고량이 차감되어 0이되면 Menu.status를 OUT_OF_STOCK으로 변경한다")
    void 재고량이_0이_되면_Menu의_status를_변경한다() {
        //given
        Menu menu = Menu.create("name", 1000);
        ReflectionTestUtils.setField(menu, "id", 1L);

        Inventory inventory = Inventory.create(menu.getId(), 10);

        given(inventoryRepository.findWithLockByMenuId(anyLong()))
                .willReturn(Optional.of(inventory));
        menu.changeStatus(MenuStatus.OUT_OF_STOCK);

        //when
        inventoryService.decreaseStock(menu.getId(), 10);

        //then
        assertEquals(0, inventory.getStock());
        assertEquals(MenuStatus.OUT_OF_STOCK, menu.getStatus());
    }
}