package com.example.cafe.ranking.presentation;

import com.example.cafe.common.ApiResponse;
import com.example.cafe.menu.model.response.MenuResponse;
import com.example.cafe.ranking.application.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {
    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getTop3MenusIn7Days() {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        rankingService.getTop3MenusIn7Days()
                )
        );
    }

}
