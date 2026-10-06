package com.example.cafe.inventory.application;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.inventory.model.Inventory;
import com.example.cafe.inventory.model.InventoryRepository;
import com.example.cafe.menu.application.MenuService;
import com.example.cafe.menu.model.MenuStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final MenuService menuService;

    @Transactional
    public void decreaseStock(Long menuId, int amount) {
        if (amount < 1) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY_AMOUNT);
        }

        Inventory inventory = getInventoryWithLock(menuId);
        inventory.decreaseInventory(amount);

        inventoryRepository.save(inventory);

        if (inventory.getStock() == 0) {
            menuService.changeStatus(menuId, MenuStatus.OUT_OF_STOCK);
        }
    }

    private Inventory getInventoryWithLock(Long menuId) {
        return inventoryRepository.findWithLockByMenuId(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));
    }
}
