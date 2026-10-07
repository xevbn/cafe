package com.example.cafe.common.aspect;

import com.example.cafe.common.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DistributedLockAspectTest {
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock lock;
    @Mock
    private DistributedLock distributedLock;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @InjectMocks
    private DistributedLockAspect aspect;

    @Test
    @DisplayName("락 획득에 성공하면 비즈니스 로직을 실행한다")
    void 락_획득에_성공하면_비즈니스_로직을_실행한다() throws Throwable {
        //given
        String key = "test-lock";

        given(distributedLock.key()).willReturn(key);
        given(distributedLock.waitTime()).willReturn(5L);
        given(distributedLock.leaseTime()).willReturn(10L);
        given(distributedLock.timeUnit()).willReturn(TimeUnit.SECONDS);
        given(redissonClient.getLock(key)).willReturn(lock);

        given(lock.tryLock(
                anyLong(),
                anyLong(),
                any()
        )).willReturn(true);

        given(joinPoint.proceed()).willReturn("success");
        given(lock.isHeldByCurrentThread()).willReturn(true);

        //when
        Object result = aspect.lock(joinPoint, distributedLock);

        //then
        assertEquals("success", result);
        verify(redissonClient).getLock(key);
        verify(lock).tryLock(
                5L, 10L, TimeUnit.SECONDS
        );
        verify(joinPoint).proceed();
        verify(lock).isHeldByCurrentThread();
        verify(lock).unlock();
    }

    @Test
    @DisplayName("비즈니스 로직에서 예외가 발생해도 락을 해제한다")
    void 비즈니스_로직에서_예외가_발생해도_락을_해제한다() throws Throwable {
        // given
        String key = "test-lock";

        given(distributedLock.key()).willReturn(key);
        given(distributedLock.waitTime()).willReturn(5L);
        given(distributedLock.leaseTime()).willReturn(10L);
        given(distributedLock.timeUnit())
                .willReturn(TimeUnit.SECONDS);

        given(redissonClient.getLock(key))
                .willReturn(lock);

        given(lock.tryLock(5L, 10L, TimeUnit.SECONDS))
                .willReturn(true);

        given(lock.isHeldByCurrentThread())
                .willReturn(true);

        RuntimeException exception =
                new RuntimeException("business error");

        given(joinPoint.proceed())
                .willThrow(exception);

        // when & then
        assertThatThrownBy(() -> aspect.lock(joinPoint, distributedLock))
                .isSameAs(exception);

        verify(joinPoint).proceed();
        verify(lock).unlock();
    }

    @Test
    @DisplayName("현재  스레드가 락을 소유하지 않으면 unlock하지 않는다")
    void 현재_스레드가_락을_소유하지_않으면_unlock하지_않는다() throws Throwable {
        // given
        String key = "test-lock";

        given(distributedLock.key()).willReturn(key);
        given(distributedLock.waitTime()).willReturn(5L);
        given(distributedLock.leaseTime()).willReturn(10L);
        given(distributedLock.timeUnit()).willReturn(TimeUnit.SECONDS);

        given(redissonClient.getLock(key))
                .willReturn(lock);

        given(lock.tryLock(5L, 10L, TimeUnit.SECONDS))
                .willReturn(true);

        given(lock.isHeldByCurrentThread())
                .willReturn(false);

        given(joinPoint.proceed())
                .willReturn("success");

        // when
        Object result = aspect.lock(joinPoint, distributedLock);

        // then
        assertEquals("success", result);

        verify(joinPoint).proceed();
        verify(lock, never()).unlock();
    }
}