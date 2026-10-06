package com.example.cafe.menu.application;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.menu.model.Menu;
import com.example.cafe.menu.model.MenuRepository;
import com.example.cafe.menu.model.MenuStatus;
import com.example.cafe.menu.model.response.MenuResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;
    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("모든 메뉴 정보를 조회해온다.")
    void 모든_메뉴_정보를_조회해온다() {
        //given
        Menu menu1 = Menu.create("name", 1000);
        List<Menu> menus = List.of(menu1);

        given(menuRepository.findAllWhereOrderStatusIsOnSale()).willReturn(menus);

        //when
        List<MenuResponse> res = menuService.getMenus();

        //then
        assertThat(res.size()).isEqualTo(1);
        assertThat(res.get(0).menuName()).isEqualTo(menu1.getName());
        assertThat(res.get(0).price()).isEqualTo(menu1.getPrice());
    }

    @Test
    @DisplayName("해당 메뉴의 상태를 변경한다")
    void 해당_메뉴의_상태를_변경한다() {
        //given
        Menu menu = Menu.create("name", 1000);

        given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
        given(menuRepository.save(any(Menu.class))).willReturn(menu);

        //when
        menuService.changeStatus(1L, MenuStatus.OUT_OF_STOCK);

        //then
        assertEquals(MenuStatus.OUT_OF_STOCK, menu.getStatus());
    }

    @Test
    @DisplayName("메뉴 상태 변경 시도 시 메뉴가 없으면 에러를 반환한다")
    void 메뉴_상태_변경_시_메뉴가_없으면_에러_반환() {
        //given
        given(menuRepository.findById(anyLong())).willReturn(Optional.empty());

        //when&then
        assertThatThrownBy(() -> menuService.changeStatus(1L, MenuStatus.OUT_OF_STOCK))
                .isInstanceOf(BusinessException.class)
                .hasMessageStartingWith(ErrorCode.MENU_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("배열의 menuId에 해당하는 메뉴 정보를 반환한다")
    void 배열_내의_menuId에_해당하는_메뉴_정보를_반환한다() {
        //given
        List<Menu> menus = List.of(
                Menu.create("name1", 1000),
                Menu.create("name2", 1000)
        );

        List<Long> menuIds = List.of(1L, 2L);

        given(menuRepository.findAllByIds(menuIds)).willReturn(menus);

        //when
        List<MenuResponse> res = menuService.getMenusInList(menuIds);

        //then
        assertEquals("name1", res.get(0).menuName());
        assertEquals("name2", res.get(1).menuName());
    }
}