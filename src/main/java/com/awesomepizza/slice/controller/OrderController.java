package com.awesomepizza.slice.controller;

import com.awesomepizza.slice.dto.CreateOrderRequest;
import com.awesomepizza.slice.dto.OrderDto;
import com.awesomepizza.slice.dto.OrderStatusResponse;
import com.awesomepizza.slice.enums.OrderStatus;
import com.awesomepizza.slice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderStatusResponse> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/{orderCode}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable String orderCode) {
        return ResponseEntity.ok(orderService.getOrderStatus(orderCode));
    }

    //region consumer

    @PatchMapping("/{orderCode}/status")
    public ResponseEntity<OrderStatusResponse> updateOrderStatus(
            @PathVariable String orderCode,
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderCode, status));
    }

    @GetMapping("/queue")
    public ResponseEntity<List<OrderDto>> getOrderQueue() {
        return ResponseEntity.ok(orderService.getOrderQueue());
    }

    @GetMapping("/next")
    public ResponseEntity<OrderDto> getNextOrder() {
        return ResponseEntity.ok(orderService.getNextOrder());
    }

    //endregion
}
