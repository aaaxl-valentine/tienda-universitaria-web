package co.unimagdalena.tiendauni.controlador;

import co.unimagdalena.tiendauni.DTOs.OrderDTOs.CreateOrderRequest;
import co.unimagdalena.tiendauni.DTOs.OrderDTOs.OrderResponse;
import co.unimagdalena.tiendauni.DTOs.OrderItemDTOs.CreateOrderItemRequest;
import co.unimagdalena.tiendauni.NotFoundException.GlobalExceptionHandler;
import co.unimagdalena.tiendauni.controller.OrderController;
import co.unimagdalena.tiendauni.entity.enums.OrderStatus;
import co.unimagdalena.tiendauni.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrderReturnsCreated() throws Exception {
        var request = new CreateOrderRequest(
                OrderStatus.CREATED,
                1L,
                1L,
                List.of(new CreateOrderItemRequest(2, null, null, 1L))
        );

        var response = new OrderResponse(1L, OrderStatus.CREATED, new BigDecimal("20.00"), LocalDateTime.now(), LocalDateTime.now(), 1L, 1L);
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createOrderWithoutItemsReturnsBadRequest() throws Exception {
        var request = new CreateOrderRequest(OrderStatus.CREATED, 1L, 1L, List.of());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void payAliasReturnsOk() throws Exception {
        var response = new OrderResponse(1L, OrderStatus.PAID, new BigDecimal("20.00"), LocalDateTime.now(), LocalDateTime.now(), 1L, 1L);
        when(orderService.processPayment(1L)).thenReturn(response);

        mockMvc.perform(put("/api/orders/1/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}
