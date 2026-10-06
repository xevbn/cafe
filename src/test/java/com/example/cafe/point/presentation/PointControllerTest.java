package com.example.cafe.point.presentation;

import com.example.cafe.common.exception.ErrorCode;
import com.example.cafe.point.application.PointService;
import com.example.cafe.point.presentation.request.PointChargeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PointController.class
)
@ActiveProfiles("test")
class PointControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PointService pointService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("포인트 충전 API 테스트")
    void 포인트_충전_API_테스트() throws Exception {
        //given
        PointChargeRequest req = new PointChargeRequest(5);

        //when&then
        mockMvc.perform(post("/api/points/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포인트 충전 API 테스트 - validation 검증 예외")
    void 포인트_충전_시_충전_포인트가_0이하면_예외가_발생한다()  throws Exception {
        //given
        Map<String, String> req = Map.of("amount", "0");

        //when&then
        mockMvc.perform(post("/api/points/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()));
    }
}