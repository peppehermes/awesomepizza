package com.awesomepizza.slice.controller;

import com.awesomepizza.slice.dto.CreateOrderRequest;
import com.awesomepizza.slice.dto.OrderDto;
import com.awesomepizza.slice.dto.OrderStatusResponse;
import com.awesomepizza.slice.enums.OrderStatus;
import com.awesomepizza.slice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderStatusResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @GetMapping("/{orderCode}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable String orderCode) {
        OrderStatusResponse response = orderService.getOrderStatus(orderCode);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(response);
    }

    //region consumer

    @PatchMapping("/{orderCode}/status")
    public ResponseEntity<OrderStatusResponse> updateOrderStatus(
            @PathVariable String orderCode,
            @RequestParam OrderStatus status
    ) {
        OrderStatusResponse response = orderService.updateOrderStatus(orderCode, status);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/queue")
    public ResponseEntity<List<OrderDto>> getOrderQueue() {
        return ResponseEntity.ok(orderService.getOrderQueue());
    }

    @GetMapping("/next")
    public ResponseEntity<OrderDto> getNextOrder() {
        return ResponseEntity.ok(orderService.getNextOrder());
    }

    /*
    TODO instead of using patch to update status of order, we can add another endpoint "/ready"
    which will update the currently PREPARING order to READY
    */

    //endregion
}
