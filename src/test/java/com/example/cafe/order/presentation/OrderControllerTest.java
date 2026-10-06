package com.example.cafe.order.presentation;

import com.example.cafe.order.application.OrderService;
import com.example.cafe.order.presentation.request.OrderCreateRequest;
import com.example.cafe.order.presentation.request.OrderItemRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@ActiveProfiles("test")
class OrderControllerTest {
    @MockitoBean
    private OrderService orderService;
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("주문 생성 api 테스트")
    void 주문_생성_api_테스트() throws Exception {
        //given
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(
                1L,
                List.of(
                        new OrderItemRequest(
                                1L,
                                1,
                                1000
                        )
                )
        );

        //when&then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isOk());
    }
}