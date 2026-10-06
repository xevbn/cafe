package com.example.cafe.ranking.application;

import com.example.cafe.menu.application.MenuService;
import com.example.cafe.menu.model.response.MenuResponse;
import com.example.cafe.ranking.infra.dto.RankingDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {
    @Mock
    private RankingCacheService rankingCacheService;
    @Mock
    private MenuService menuService;
    @InjectMocks
    private RankingService rankingService;

    @Test
    @DisplayName("최근 7일 간 주문량 상위 3개 메뉴 정보를 반환한다")
    void getTop3MenusIn7Days() {
        //given
        MenuResponse menuResponse = new MenuResponse(
                1L,
                "name",
                1000
        );

        given(rankingCacheService.findMenuRankingTop3In7Days())
                .willReturn(List.of(1L));
        given(menuService.getMenusInList(any())).willReturn(List.of(menuResponse));

        //when
        List<MenuResponse> result = rankingService.getTop3MenusIn7Days();

        //then
        assertNotNull(result);
        assertEquals("name", result.get(0).menuName());
        assertEquals(1000, result.get(0).price());
    }
}