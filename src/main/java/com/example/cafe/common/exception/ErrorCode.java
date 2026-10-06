package com.example.cafe.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // Common (COMMON_xxx)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "유효하지 않은 입력값입니다."),

    // Menu (MENU_xxx)
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_001", "해당 메뉴를 찾을 수 없습니다."),

    // Inventory (INVENTORY_xxx)
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "INVENTORY_001", "해당 상품의 재고를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "INVENTORY_002", "재고 수량이 부족합니다."),

    // Order (ORDER_xxx)
    INVALID_QUANTITY_AMOUNT(HttpStatus.BAD_REQUEST, "ORDER_001", "주문 수량은 0 이하일 수 없습니다."),

    // Point (POINT_xxx)
    USER_POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "POINT_001", "해당 사용자를 찾을 수 없습니다."),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "POINT_002", "포인트 변동은 0보다 커야 합니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "POINT_003", "포인트 잔액이 부족합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
