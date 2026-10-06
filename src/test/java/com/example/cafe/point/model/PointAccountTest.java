package com.example.cafe.point.model;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PointAccountTest {

    @Test
    @DisplayName("충전 금액이 0보다 크면 충전이 되어야 한다.")
    void 충전금액이_0보다_크면_포인트를_증가시킨다() {
        //given
        PointAccount pointAccount = PointAccount.create(1L);

        //when&then
        pointAccount.chargePoint(1000);

        assertEquals(1000, pointAccount.getBalance());
    }

    @Test
    @DisplayName("충전 금액이 0이하면 예외를 반환한다")
    void 충전금액이_0이하면_예외를_반환한다() {
        //given
        PointAccount pointAccount = PointAccount.create(1L);

        //when&then
        assertThatThrownBy(() -> pointAccount.chargePoint(0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_POINT_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("포인트 수가 충분하다면 포인트를 차감한다")
    void 포인트_수가_충분하다면_포인트를_차감한다() {
        //given
        PointAccount pointAccount = PointAccount.create(1L);

        pointAccount.chargePoint(1000);

        //when&then
        pointAccount.decreaseBalance(100);

        assertEquals(900,  pointAccount.getBalance());
    }

    @Test
    @DisplayName("포인트 수가 부족하다면 에러를 반환한다")
    void 포인트_수가_부족하다면_에러를_반환한다() {
        //given
        PointAccount pointAccount = PointAccount.create(1L);

        //when&then
        assertThatThrownBy(() -> pointAccount.decreaseBalance(100))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_POINT.getMessage());
    }
}