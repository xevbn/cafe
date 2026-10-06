import com.example.cafe.menu.application.MenuService;
import com.example.cafe.menu.model.Menu;
import com.example.cafe.menu.model.MenuRepository;
import com.example.cafe.menu.model.response.MenuResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
}