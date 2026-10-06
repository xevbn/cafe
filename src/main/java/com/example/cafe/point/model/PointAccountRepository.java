package com.example.cafe.point.model;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PointAccountRepository extends JpaRepository<PointAccount, Long> {
    Optional<PointAccount> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PointAccount> findWithLockByUserId(Long userId);
}
