package com.example.cafe.ranking.application;

import com.example.cafe.menu.application.MenuService;
import com.example.cafe.menu.model.response.MenuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingCacheService rankingCacheService;
    private final MenuService menuService;

    public List<MenuResponse> getTop3MenusIn7Days() {
        List<Long> top3MenuIds = rankingCacheService.findMenuRankingTop3In7Days();

        return menuService.getMenusInList(top3MenuIds);
    }
}
