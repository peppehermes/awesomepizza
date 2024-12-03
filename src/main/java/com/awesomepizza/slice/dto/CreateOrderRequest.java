package com.awesomepizza.slice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class CreateOrderRequest {
    @NotBlank(message = "pizzaType should not be empty, null or whitespace")
    private String pizzaType;

    @Positive(message = "quantity should be positive")
    @NotNull(message = "quantity should not be null")
    private int quantity;
}
