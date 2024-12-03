package com.awesomepizza.slice.entity;

import com.awesomepizza.slice.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pizza_orders")
public class PizzaOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderCode;

    @NotBlank(message = "pizzaType should not be empty, null or whitespace")
    private String pizzaType;

    @Positive(message = "quantity should be positive")
    @NotNull(message = "quantity should not be null")
    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime insertTimestamp;
    private LocalDateTime updateTimestamp;
}
