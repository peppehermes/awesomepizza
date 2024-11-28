package com.awesomepizza.slice.dto;

import com.awesomepizza.slice.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDto {
    private String orderCode;
    private String pizzaType;
    private int quantity;
    private OrderStatus status;
    private LocalDateTime insertTimestamp;
}
