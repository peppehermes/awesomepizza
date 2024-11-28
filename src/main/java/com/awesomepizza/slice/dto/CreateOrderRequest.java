package com.awesomepizza.slice.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String pizzaType;
    private int quantity;
}
