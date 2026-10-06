package com.example.cafe.point.application;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.point.application.PointService;
import com.example.cafe.point.model.PointAccount;
import com.example.cafe.point.model.PointAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PointServiceTest {
    @Mock
    private PointAccountRepository pointAccountRepository;
    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("사용자의 포인트가 존재하고 충전하고자 하는 포인트가 0보다 크면 충전할 수 있다.")
    void 사용자의_포인트가_존재하고_충전_포인트가_0보다_크면_충전할_수_있다() {
        //given
        PointAccount pointAccount = PointAccount.create(1L);

        given(pointAccountRepository.findByUserId(anyLong())).willReturn(Optional.of(pointAccount));
        given(pointAccountRepository.save(any(PointAccount.class))).willReturn(pointAccount);

        //when
        pointService.chargePoint(1L, 1000);

        //then
        assertEquals(1000, pointAccount.getBalance());
    }

    @Test
    @DisplayName("충전하고자 하는 포인트가 0이하이면 충전할 수 없다.")
    void 충전_포인트가_0이하면_충전할_수_없다() {
        //when&then
        assertThatThrownBy(() -> pointService.chargePoint(1L, 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_POINT_AMOUNT.getMessage());
    }
}