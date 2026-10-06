package com.example.cafe.menu.presentation;

import com.example.cafe.menu.application.MenuService;
import com.example.cafe.menu.model.response.MenuResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MenuController.class
)
@ActiveProfiles("test")
class MenuControllerTest {
    @MockitoBean
    private MenuService menuService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("메뉴 전체 조회 api 테스트")
    void getAllMenus() throws Exception {
        //given
        MenuResponse MenuResponse = new MenuResponse(
                1L,
                "name",
                1000
        );

        given(menuService.getMenus()).willReturn(List.of(MenuResponse));

        //when & then
        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.[0].menuName").value("name"))
                .andExpect(jsonPath("data.[0].price").value(1000));
    }
}