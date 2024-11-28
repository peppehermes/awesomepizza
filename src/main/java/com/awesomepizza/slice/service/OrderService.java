package com.awesomepizza.slice.service;

import com.awesomepizza.slice.dto.CreateOrderRequest;
import com.awesomepizza.slice.dto.OrderDto;
import com.awesomepizza.slice.dto.OrderStatusResponse;
import com.awesomepizza.slice.entity.PizzaOrder;
import com.awesomepizza.slice.enums.OrderStatus;
import com.awesomepizza.slice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    //region public methods

    public OrderStatusResponse createOrder(CreateOrderRequest request) {
        PizzaOrder order = new PizzaOrder();

        PizzaOrder savedOrder = orderRepository.save(order);

        return mapToOrderStatusResponse(savedOrder);
    }

    public OrderStatusResponse getOrderStatus(String orderCode) {
        PizzaOrder order = getPizzaOrder(orderCode);

        return mapToOrderStatusResponse(order);
    }

    public OrderStatusResponse updateOrderStatus(String orderCode, OrderStatus newStatus) {
        PizzaOrder order = getPizzaOrder(orderCode);

        // Check if the order status is RECEIVED or PREPARING
        // Otherwise, if it is in READY state it cannot be updated
        if (order.getStatus().equals(OrderStatus.READY)) {
            throw new RuntimeException("Order in READY state, cannot update");
        }

        order.setStatus(newStatus);
        order.setUpdateTimestamp(LocalDateTime.now());

        PizzaOrder updatedOrder = orderRepository.save(order);
        return mapToOrderStatusResponse(updatedOrder);
    }

    public List<OrderDto> getOrderQueue() {
        List<PizzaOrder> queuedOrders = orderRepository.findByStatusOrderOrderByCreatedAtAsc(OrderStatus.RECEIVED);

        return queuedOrders.stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    public OrderDto getNextOrder() {
        // If nextOrder is called multiple times, first we need to check if an order is actually in PREPARING state
        Optional<PizzaOrder> currentOrder = orderRepository.findByStatus(OrderStatus.PREPARING);

        if (currentOrder.isPresent()) {
            return mapToOrderDto(currentOrder.get());
        }

        // If no order is in PREPARING, then get the next RECEIVED order
        List<PizzaOrder> queuedOrders = orderRepository.findByStatusOrderOrderByCreatedAtAsc(OrderStatus.RECEIVED);

        // If the queue is empty, then return an error
        if (queuedOrders.isEmpty()) {
            throw new RuntimeException("No order in queue");
        }

        PizzaOrder nextOrder = queuedOrders.get(0);
        nextOrder.setStatus(OrderStatus.PREPARING);
        nextOrder.setUpdateTimestamp(LocalDateTime.now());
        return mapToOrderDto(nextOrder);
    }

    //endregion

    //region private methods

    private PizzaOrder getPizzaOrder(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    private OrderStatusResponse mapToOrderStatusResponse(PizzaOrder order) {
        OrderStatusResponse response = new OrderStatusResponse();
        BeanUtils.copyProperties(order, response);
        return response;
    }

    private OrderDto mapToOrderDto(PizzaOrder order) {
        OrderDto dto = new OrderDto();
        BeanUtils.copyProperties(order, dto);
        return dto;
    }

    //endregion
}
