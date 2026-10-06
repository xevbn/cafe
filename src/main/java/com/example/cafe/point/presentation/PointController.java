package com.example.cafe.point.presentation;

import com.example.cafe.common.ApiResponse;
import com.example.cafe.point.application.PointService;
import com.example.cafe.point.presentation.request.PointChargeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> chargePoint(@PathVariable Long userId, @Valid @RequestBody PointChargeRequest req) {
        pointService.chargePoint(userId, req.amount());

        return ResponseEntity.ok(ApiResponse.ok());
    }
}
