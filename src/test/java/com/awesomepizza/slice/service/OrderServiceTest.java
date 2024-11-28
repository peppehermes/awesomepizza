package com.awesomepizza.slice.service;

import com.awesomepizza.slice.dto.CreateOrderRequest;
import com.awesomepizza.slice.dto.OrderDto;
import com.awesomepizza.slice.dto.OrderStatusResponse;
import com.awesomepizza.slice.entity.PizzaOrder;
import com.awesomepizza.slice.enums.OrderStatus;
import com.awesomepizza.slice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository);
    }

    @Test
    void createOrder_ShouldCreateNewOrder() {
        // Create mock order request
        CreateOrderRequest request = new CreateOrderRequest();
        request.setPizzaType("Margherita");
        request.setQuantity(1);

        // Create mock saved order
        PizzaOrder savedOrder = new PizzaOrder();
        savedOrder.setOrderCode("TEST123");
        savedOrder.setStatus(OrderStatus.RECEIVED);

        // Set what to save when repository save is invoked
        when(orderRepository.save(any(PizzaOrder.class))).thenReturn(savedOrder);

        // Call service (which will invoke mock repository) to create new order
        OrderStatusResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(OrderStatus.RECEIVED, response.getStatus());
        assertNotNull(response.getOrderCode());

        verify(orderRepository, times(1)).save(any(PizzaOrder.class));
    }

    @Test
    void getOrderStatus_ShoudlReturnOrderStatus() {
        // Create an existing order
        PizzaOrder existingOrder = new PizzaOrder();
        existingOrder.setOrderCode("TEST123");
        existingOrder.setStatus(OrderStatus.RECEIVED);

        // Set what to return when the order is searched in the repository
        when(orderRepository.findByOrderCode("TEST123")).thenReturn(Optional.of(existingOrder));

        // Call service
        OrderStatusResponse response = orderService.getOrderStatus("TEST123");

        assertEquals(OrderStatus.RECEIVED, response.getStatus());
        assertEquals("TEST123", response.getOrderCode());
    }

    @Test
    void updateOrderStatus_ShouldUpdateOrderStatus() {
        // Create an existing order
        PizzaOrder existingOrder = new PizzaOrder();
        existingOrder.setOrderCode("TEST123");
        existingOrder.setStatus(OrderStatus.RECEIVED);

        // Set what to return when the order is searched in the repository
        when(orderRepository.findByOrderCode("TEST123")).thenReturn(Optional.of(existingOrder));

        // Set what to save when repository save is invoked
        when(orderRepository.save(any(PizzaOrder.class))).thenReturn(existingOrder);

        // Call service (which will invoke mock repository) to update existing order
        OrderStatusResponse response = orderService.updateOrderStatus("TEST123", OrderStatus.PREPARING);

        assertEquals(OrderStatus.PREPARING, response.getStatus());

        verify(orderRepository, times(1)).save(any(PizzaOrder.class));
    }

    @Test
    void getOrderQueue_ShoudlReturnQueuedOrders() {
        // Create existing orders
        PizzaOrder order1 = new PizzaOrder();
        order1.setOrderCode("TEST123");
        order1.setStatus(OrderStatus.RECEIVED);
        order1.setQuantity(1);
        order1.setPizzaType("Margherita");
        order1.setInsertTimestamp(LocalDateTime.now().minusMinutes(10));

        PizzaOrder order2 = new PizzaOrder();
        order2.setOrderCode("TEST456");
        order2.setStatus(OrderStatus.RECEIVED);
        order2.setQuantity(2);
        order2.setPizzaType("Capricciosa");
        order2.setInsertTimestamp(LocalDateTime.now().minusMinutes(5));

        // Set what to save when repository search is invoked
        when(orderRepository.findByStatusOrderOrderByCreatedAtAsc(OrderStatus.RECEIVED))
                .thenReturn(Arrays.asList(order1, order2));

        // Call service
        List<OrderDto> queue = orderService.getOrderQueue();

        assertEquals(2, queue.size());
        assertEquals("TEST123", queue.get(0).getOrderCode());
        assertEquals("TEST456", queue.get(1).getOrderCode());
    }

    // TODO
    @Test
    void getNextOrder_ShouldReturnNextOrder() { }

}
