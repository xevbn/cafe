package com.example.cafe.order.application;

import com.example.cafe.common.exception.BusinessException;
import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.inventory.application.InventoryService;
import com.example.cafe.order.application.command.CreateOrderCommand;
import com.example.cafe.order.application.command.CreateOrderItemCommand;
import com.example.cafe.order.model.Order;
import com.example.cafe.order.model.OrderRepository;
import com.example.cafe.outbox.application.OutboxService;
import com.example.cafe.point.application.PointService;
import com.example.cafe.ranking.application.RankingCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PointService pointService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private OutboxService outboxService;
    @Mock
    private RankingCacheService rankingCacheService;
    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문을 생성")
    void createOrder() {
        //given
        CreateOrderCommand command = new CreateOrderCommand(
                1L,
                List.of(
                        new CreateOrderItemCommand(
                                1L,
                                2,
                                1000
                        ),
                        new CreateOrderItemCommand(
                                2L,
                                1,
                                5000
                        )
                )
        );

        //when
        orderService.createOrder(command);

        //then
        verify(inventoryService, times(2)).decreaseStock(anyLong(), anyInt());
        verify(pointService).decreaseBalance(anyLong(), anyInt());
        verify(orderRepository).save(any(Order.class));
        verify(rankingCacheService).increaseMenusRanking(anyList());
        verify(outboxService).saveOutbox(any(), any(), anyString());
    }

    @Test
    @DisplayName("주문 생성 시 주문 수량이 0 이하면 예외가 발생한다")
    void 주문_생성_시_주문_수량이_0_이하면_예외를_던진다() {
        //given
        CreateOrderCommand command = new CreateOrderCommand(
                1L,
                List.of(
                        new CreateOrderItemCommand(
                                1L,
                                0,
                                1000
                        ),
                        new CreateOrderItemCommand(
                                2L,
                                1,
                                5000
                        )
                )
        );

        //when&then
        assertThatThrownBy(() -> orderService.createOrder(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_QUANTITY_AMOUNT.getMessage());
    }
}