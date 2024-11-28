package com.awesomepizza.slice.repository;


import com.awesomepizza.slice.entity.PizzaOrder;
import com.awesomepizza.slice.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<PizzaOrder, Long> {
    Optional<PizzaOrder> findByOrderCode(String orderCode);

    List<PizzaOrder> findByStatusOrderOrderByCreatedAtAsc(OrderStatus status);

    Optional<PizzaOrder> findByStatus(OrderStatus status);
}
