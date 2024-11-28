package com.awesomepizza.slice.dto;

import com.awesomepizza.slice.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusResponse {
    private String orderCode;
    private OrderStatus status;
}
