package com.example.cafe.order.presentation;

import com.example.cafe.common.ApiResponse;
import com.example.cafe.order.application.OrderService;
import com.example.cafe.order.application.command.CreateOrderCommand;
import com.example.cafe.order.presentation.request.OrderCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createOrder(@RequestBody OrderCreateRequest request) {
        orderService.createOrder(request.toCreateOrderCommand());

        return ResponseEntity.ok(ApiResponse.ok());
    }
}
