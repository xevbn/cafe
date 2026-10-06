package com.example.cafe.ranking.infra;

import com.example.cafe.TestcontainersConfiguration;
import com.example.cafe.common.config.redis.RedisConfig;
import com.example.cafe.ranking.application.RankingCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.cafe.ranking.infra.RankingRedisCacheService.MENU_RANKING_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import({
        TestcontainersConfiguration.class,
        RedisConfig.class
})
class RankingRedisCacheServiceTest {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RankingCacheService rankingCacheService;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> "kafka");
    }

    @Test
    @DisplayName("해당 메뉴 ID들의 점수를 1씩 올린다")
    void 해당_메뉴들의_점수를_1씩_올린다() {
        //given
        String key = MENU_RANKING_KEY + LocalDate.now();
        redisTemplate.opsForZSet().incrementScore(key, "2", 1);

        //when
        rankingCacheService.increaseMenusRanking(List.of(1L, 2L));

        //then
        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, 5);

        Map<String, Double> resultMap = result.stream()
                .collect(Collectors.toMap(
                        ZSetOperations.TypedTuple::getValue,
                        ZSetOperations.TypedTuple::getScore
                ));

        assertEquals(1, resultMap.get("1"));
        assertEquals(2, resultMap.get("2"));
    }

    @Test
    @DisplayName("최근 7일 내 주문량이 가장 많은 메뉴 3개의 정보를 가져온다")
    void 최근_7일간_주문량이_가장_많은_메뉴의_정보를_반환한다() {
        //given
        for (int i = 0; i < 8; i++) {
            String key = MENU_RANKING_KEY + LocalDate.now().minusDays(i);
            redisTemplate.opsForZSet().incrementScore(key, String.valueOf(i % 5), i);
        }

        //when&then
        List<Long> result = rankingCacheService.findMenuRankingTop3In7Days();

        assertEquals(3, result.size());
        assertEquals(2, result.get(0));
        assertEquals(1, result.get(1));
        assertEquals(0, result.get(2));
    }
}