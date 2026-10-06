package com.example.cafe.menu.model.response;

import com.example.cafe.menu.model.Menu;

public record MenuResponse(
        Long menuId,
        String menuName,
        int price
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(menu.getId(), menu.getName(), menu.getPrice());
    }
}
