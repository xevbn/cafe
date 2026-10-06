package com.example.cafe.menu.application;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.menu.model.Menu;
import com.example.cafe.menu.model.MenuRepository;
import com.example.cafe.menu.model.MenuStatus;
import com.example.cafe.menu.model.response.MenuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenus() {
        return menuRepository.findAllWhereOrderStatusIsOnSale().stream()
                .map(MenuResponse::from)
                .toList();
    }

    @Transactional
    public void changeStatus(Long menuId, MenuStatus status) {
        Menu found = menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        found.changeStatus(status);

        menuRepository.save(found);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenusInList(List<Long> menuIds) {
        return menuRepository.findAllByIds(menuIds).stream()
                .map(MenuResponse::from)
                .toList();
    }
}
