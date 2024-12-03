package com.awesomepizza.slice.controller;

import com.awesomepizza.slice.dto.CreateOrderRequest;
import com.awesomepizza.slice.dto.OrderDto;
import com.awesomepizza.slice.dto.OrderStatusResponse;
import com.awesomepizza.slice.enums.OrderStatus;
import com.awesomepizza.slice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_ShouldReturnOrderStatusResponse() throws Exception {
        // Create new mock request
        CreateOrderRequest request = new CreateOrderRequest();
        request.setPizzaType("Margherita");
        request.setQuantity(1);

        // Create new mock response
        OrderStatusResponse response = new OrderStatusResponse();
        response.setStatus(OrderStatus.RECEIVED);
        response.setOrderCode("TEST123");

        // Set what to receive when mocked service is called
        when(orderService.createOrder(request)).thenReturn(response);

        // Perform API request
        mockMvc.perform(
                post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderCode").value("TEST123"))
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void createOrder_ShouldReturnBadRequest() throws Exception {
        // Create new mock request
        CreateOrderRequest request = new CreateOrderRequest();
        request.setPizzaType("Margherita");
        request.setQuantity(-1);

        // Create new mock response
        ResponseEntity<OrderStatusResponse> response =
                new ResponseEntity<>(orderService.createOrder(request), HttpStatus.BAD_REQUEST);

        // Set what to receive when mocked service is called
        when(orderService.createOrder(request)).thenReturn(response.getBody());

        // Perform API request
        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("quantity should be positive"));
    }

    @Test
    void getOrderStatus_ShoudlReturnOrderStatus() throws Exception {
        // Create new mock response
        String orderCode = "TEST123";
        OrderStatus status = OrderStatus.RECEIVED;

        OrderStatusResponse response = new OrderStatusResponse();
        response.setOrderCode(orderCode);
        response.setStatus(status);

        // Set what to receive when mocked service is called
        when(orderService.getOrderStatus(orderCode)).thenReturn(response);

        // Perform API request
        mockMvc.perform(get("/api/orders/{orderCode}/status", orderCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCode").value(orderCode))
                .andExpect(jsonPath("$.status").value(status.toString()));
    }

    // TODO test for get on order not found

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrderStatus() throws Exception {
        // Create new mock response
        String orderCode = "TEST123";
        OrderStatus status = OrderStatus.PREPARING;

        OrderStatusResponse response = new OrderStatusResponse();
        response.setOrderCode(orderCode);
        response.setStatus(status);

        // Set what to receive when mocked service is called
        when(orderService.updateOrderStatus(orderCode, status)).thenReturn(response);

        // Perform API request
        mockMvc.perform(
                patch("/api/orders/{orderCode}/status", orderCode)
                        .param("status", status.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCode").value("TEST123"))
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    // TODO test for patch on order not found

    @Test
    void getOrderQueue_ShouldReturnQueuedOrders() throws Exception {
        // Create existing orders
        OrderDto order1 = new OrderDto();
        order1.setOrderCode("TEST123");
        order1.setStatus(OrderStatus.RECEIVED);
        order1.setQuantity(1);
        order1.setPizzaType("Margherita");
        order1.setInsertTimestamp(LocalDateTime.now().minusMinutes(10));

        OrderDto order2 = new OrderDto();
        order2.setOrderCode("TEST456");
        order2.setStatus(OrderStatus.RECEIVED);
        order2.setQuantity(2);
        order2.setPizzaType("Capricciosa");
        order2.setInsertTimestamp(LocalDateTime.now().minusMinutes(5));

        // Set what to receive when mocked service is called
        when(orderService.getOrderQueue()).thenReturn(Arrays.asList(order1, order2));

        // Perform API request
        mockMvc.perform(get("/api/orders/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderCode").value("TEST123"))
                .andExpect(jsonPath("$[0].pizzaType").value("Margherita"))
                .andExpect(jsonPath("$[1].orderCode").value("TEST456"))
                .andExpect(jsonPath("$[1].pizzaType").value("Capricciosa"));
    }

    @Test
    void getOrderQueue_ShouldReturnEmptyList() throws Exception {
        // Set what to receive when mocked service is called
        when(orderService.getOrderQueue()).thenReturn(List.of());

        // Perform API request
        mockMvc.perform(get("/api/orders/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getNextOrder_ShouldReturnNextOrder() throws Exception {
        // Create existing orders
        OrderDto order1 = new OrderDto();
        order1.setOrderCode("TEST123");
        order1.setStatus(OrderStatus.PREPARING);
        order1.setQuantity(1);
        order1.setPizzaType("Margherita");
        order1.setInsertTimestamp(LocalDateTime.now().minusMinutes(10));

        when(orderService.getNextOrder()).thenReturn(order1);

        mockMvc.perform(get("/api/orders/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCode").value("TEST123"))
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }
}
