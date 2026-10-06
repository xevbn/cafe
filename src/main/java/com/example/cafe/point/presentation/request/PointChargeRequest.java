package com.example.cafe.point.presentation.request;

import jakarta.validation.constraints.Min;

public record PointChargeRequest(
        @Min(1)
        Integer amount
) {
}
