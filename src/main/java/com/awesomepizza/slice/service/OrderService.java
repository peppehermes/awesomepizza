package com.awesomepizza.slice.service;

import com.awesomepizza.slice.dto.CreateOrderRequest;
import com.awesomepizza.slice.dto.OrderDto;
import com.awesomepizza.slice.dto.OrderStatusResponse;
import com.awesomepizza.slice.entity.PizzaOrder;
import com.awesomepizza.slice.enums.OrderStatus;
import com.awesomepizza.slice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final static Logger LOGGER = LogManager.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    //region public methods

    public OrderStatusResponse createOrder(CreateOrderRequest request) {
        PizzaOrder order = new PizzaOrder();

        order.setOrderCode(generateUniqueOrderCode());
        order.setPizzaType(request.getPizzaType());
        order.setQuantity(request.getQuantity());
        order.setStatus(OrderStatus.RECEIVED);
        order.setInsertTimestamp(LocalDateTime.now());
        order.setUpdateTimestamp(LocalDateTime.now());

        PizzaOrder savedOrder = orderRepository.save(order);

        return mapToOrderStatusResponse(savedOrder);
    }

    public OrderStatusResponse getOrderStatus(String orderCode) {
        PizzaOrder order = getPizzaOrder(orderCode);

        if (order == null) {
            LOGGER.warn("Order {} not found", orderCode);
            return null;
        }

        return mapToOrderStatusResponse(order);
    }

    public OrderStatusResponse updateOrderStatus(String orderCode, OrderStatus newStatus) {
        PizzaOrder order = getPizzaOrder(orderCode);

        // Check if the order status is RECEIVED or PREPARING
        // Otherwise, if it is in READY state it cannot be updated
        if (order.getStatus().equals(OrderStatus.READY)) {
            LOGGER.warn("Order {} in READY state, cannot update", order.getOrderCode());
            return null;
        }

        order.setStatus(newStatus);
        order.setUpdateTimestamp(LocalDateTime.now());

        PizzaOrder updatedOrder = orderRepository.save(order);
        return mapToOrderStatusResponse(updatedOrder);
    }

    public List<OrderDto> getOrderQueue() {
        List<PizzaOrder> queuedOrders = orderRepository.findByStatusOrderByInsertTimestampAsc(OrderStatus.RECEIVED);

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
        List<PizzaOrder> queuedOrders = orderRepository.findByStatusOrderByInsertTimestampAsc(OrderStatus.RECEIVED);

        // If the queue is empty or null, then return null
        if (queuedOrders == null || queuedOrders.isEmpty()) {
            LOGGER.warn("No order in queue");
            return null;
        }

        PizzaOrder nextOrder = queuedOrders.get(0);
        nextOrder.setStatus(OrderStatus.PREPARING);
        nextOrder.setUpdateTimestamp(LocalDateTime.now());

        PizzaOrder updatedOrder = orderRepository.save(nextOrder);
        return mapToOrderDto(updatedOrder);
    }

    //endregion

    //region private methods

    private PizzaOrder getPizzaOrder(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElse(null);
    }

    private String generateUniqueOrderCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
