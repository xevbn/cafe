package com.example.cafe.ranking.infra;

import com.example.cafe.ranking.application.RankingCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingRedisCacheService implements RankingCacheService {
    public static final String MENU_RANKING_KEY = "menu:ranking:";
    private final StringRedisTemplate redisTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void increaseMenusRanking(List<Long> menuIds) {
        menuIds.forEach(menuId -> {
            LocalDate today = LocalDate.now();
            String key = MENU_RANKING_KEY + today;

            redisTemplate.opsForZSet().incrementScore(key, menuId.toString(), 1);
        });
    }

    public List<Long> findMenuRankingTop3In7Days() {
        LocalDate today = LocalDate.now();
        String destination = MENU_RANKING_KEY + "last-7-days";

        if (!redisTemplate.hasKey(destination)) {
            List<String> keys = new ArrayList<>();

            for (int i = 7; i > 0; i--) {
                keys.add(MENU_RANKING_KEY + today.minusDays(i));
            }

            redisTemplate.opsForZSet()
                    .unionAndStore(
                            keys.get(0),
                            keys.subList(1, keys.size()),
                            destination
                    );
        }

        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet()
                .reverseRangeWithScores(destination, 0, 2);

        if (result == null) {
            return Collections.emptyList();
        }

        return result.stream()
                .map(tuple -> Long.parseLong(Objects.requireNonNull(tuple.getValue())))
                .toList();
    }
}
