package com.example.cafe.concurrency;

import com.example.cafe.TestcontainersConfiguration;
import com.example.cafe.inventory.application.InventoryService;
import com.example.cafe.inventory.model.Inventory;
import com.example.cafe.inventory.model.InventoryRepository;
import com.example.cafe.menu.model.Menu;
import com.example.cafe.menu.model.MenuRepository;
import com.example.cafe.point.application.PointService;
import com.example.cafe.point.model.PointAccount;
import com.example.cafe.point.model.PointAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class ConcurrencyTest {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private PointService pointService;
    @Autowired
    private PointAccountRepository pointAccountRepository;

    @Test
    @DisplayName("동시에 재고를 차감해도 정합성이 보장된다")
    void 동시에_재고를_차감해도_정합성이_보장된다() throws Exception {
        //given
        Menu menu = menuRepository.saveAndFlush(Menu.create("name", 1000));
        inventoryRepository.saveAndFlush(
                Inventory.create(menu.getId(), 20)
        );

        int threadCount = 20;

        ExecutorService executor = Executors.newFixedThreadPool(20);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        List<Throwable> exceptions = new CopyOnWriteArrayList<>();

        //when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                ready.countDown();

                try {
                    start.await();

                    inventoryService.decreaseStock(menu.getId(), 1);
                } catch (Throwable e) {
                    exceptions.add(e);
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        boolean completed = done.await(30, TimeUnit.SECONDS);

        if (!completed) {
            throw new AssertionError("동시성 테스트 시간 초과");
        }

        //then
        Inventory found = inventoryService.findByMenuId(menu.getId());

        assertEquals(0, exceptions.size());
        assertEquals(0, found.getStock());
    }

    @Test
    @DisplayName("동시에 포인트를 차감해도 정합성이 보장된다")
    void 동시에_포인트를_차감해도_정합성이_보장된다() throws Exception {
        //given
        pointAccountRepository.saveAndFlush(PointAccount.create(1L));
        pointService.chargePoint(1L, 2000);

        int threadCount = 20;

        ExecutorService executor = Executors.newFixedThreadPool(20);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        List<Throwable> exceptions = new CopyOnWriteArrayList<>();

        //when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                ready.countDown();

                try {
                    start.await();

                    pointService.decreaseBalance(1L, 100);
                } catch (Throwable e) {
                    exceptions.add(e);
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        boolean completed = done.await(30, TimeUnit.SECONDS);

        if (!completed) {
            throw new AssertionError("동시성 테스트 시간 초과");
        }

        //then
        PointAccount point = pointService.getPointAccountByUserId(1L);

        assertEquals(0, exceptions.size());
        assertEquals(0, point.getBalance());
    }
}
