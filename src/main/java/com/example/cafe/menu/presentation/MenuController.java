package com.example.cafe.menu.presentation;

import com.example.cafe.common.ApiResponse;
import com.example.cafe.menu.application.MenuService;
import com.example.cafe.menu.model.response.MenuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAllMenus() {
        return ResponseEntity.ok(
                ApiResponse.ok(menuService.getMenus())
        );
    }
}
