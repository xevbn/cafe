package com.example.cafe.point.application;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.point.model.PointAccount;
import com.example.cafe.point.model.PointAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointAccountRepository pointAccountRepository;

    @Transactional
    public void chargePoint(Long userId, int amount) {
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }

        PointAccount pointAccount = getPointAccountByUserId(userId);

        pointAccount.chargePoint(amount);
        pointAccountRepository.save(pointAccount);
    }

    @Transactional
    public void decreaseBalance(Long userId, int amount) {
        PointAccount pointAccount = pointAccountRepository.findWithLockByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_POINT_NOT_FOUND));

        pointAccount.decreaseBalance(amount);
        pointAccountRepository.save(pointAccount);
    }

    private PointAccount getPointAccountByUserId(Long userId) {
        return pointAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_POINT_NOT_FOUND));
    }
}
