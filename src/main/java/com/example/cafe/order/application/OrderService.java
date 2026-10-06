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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PointService pointService;
    private final InventoryService inventoryService;
    private final OutboxService outboxService;
    private final RankingCacheService rankingCacheService;

    @Transactional
    public void createOrder(CreateOrderCommand createOrderCommand) {
        //개별 메뉴 정보
        List<CreateOrderItemCommand> items = createOrderCommand.items();

        //총 가격 합
        int totalPrice = items.stream()
                .mapToInt(item -> item.price() * item.quantity())
                .sum();

        //재고 차감
        items.forEach(item -> {
            if (item.quantity() < 1) {
                throw new BusinessException(ErrorCode.INVALID_QUANTITY_AMOUNT);
            }

            inventoryService.decreaseStock(item.menuId(), item.quantity());
        });

        //포인트 차감
        pointService.decreaseBalance(createOrderCommand.userId(), totalPrice);

        //주문 생성
        Order order = Order.create(
                createOrderCommand.userId(),
                items.stream()
                        .map(CreateOrderItemCommand::toOrderItemData)
                        .toList(),
                totalPrice
        );

        orderRepository.save(order);

        //event 저장을 위한 payload 생성
        Map<String, Object> payload = Map.of(
                "order_num", order.getOrderNum(),
                "items", order.getOrderItems().stream()
                        .map(item ->
                            Map.of("menu_id", item.getMenuId(),
                                    "quantity", item.getQuantity())
                        ).toList(),
                "total_price", totalPrice
        );


        //주문에 따른 랭킹 점수 상승
        List<Long> menuIds = items.stream()
                .map(CreateOrderItemCommand::menuId)
                .toList();
        rankingCacheService.increaseMenusRanking(menuIds);

        //outboxEvent 저장
        outboxService.saveOutbox(order.getId(), payload, "Order");
    }
}
