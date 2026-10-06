package com.example.cafe.point.model;

import com.example.cafe.common.entity.BaseEntity;
import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int balance;

    @Column(unique = true)
    private Long userId;

    private PointAccount(Long userId) {
        this.userId = userId;
        this.balance = 0;
    }

    public static PointAccount create(Long userId) {
        return new PointAccount(userId);
    }

    public void chargePoint(int amount) {
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }

        this.balance += amount;
    }

    public void decreaseBalance(int amount) {
        if (this.balance < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }

        this.balance -= amount;
    }
}
