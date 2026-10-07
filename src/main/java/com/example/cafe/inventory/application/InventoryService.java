package com.example.cafe.inventory.application;

import com.example.cafe.common.annotation.DistributedLock;
import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.inventory.model.Inventory;
import com.example.cafe.inventory.model.InventoryRepository;
import com.example.cafe.menu.application.MenuService;
import com.example.cafe.menu.model.MenuStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final MenuService menuService;

    @DistributedLock(key = "'menu:' + #menuId")
    @Transactional
    public void decreaseStock(Long menuId, int amount) {
        if (amount < 1) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY_AMOUNT);
        }

        Inventory inventory = getInventoryWithLock(menuId);
        inventory.decreaseInventory(amount);

        inventoryRepository.save(inventory);

        log.info("decrease stock successfully.");

        if (inventory.getStock() == 0) {
            menuService.changeStatus(menuId, MenuStatus.OUT_OF_STOCK);
        }
    }

    @Transactional(readOnly = true)
    public Inventory findByMenuId(Long menuId) {
        return inventoryRepository.findByMenuId(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    private Inventory getInventoryWithLock(Long menuId) {
        return inventoryRepository.findWithLockByMenuId(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVENTORY_NOT_FOUND));
    }
}
