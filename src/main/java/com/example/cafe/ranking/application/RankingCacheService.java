package com.example.cafe.ranking.application;

import java.util.List;

public interface RankingCacheService {
    void increaseMenusRanking(List<Long> menuIds);
    List<Long> findMenuRankingTop3In7Days();
}
