package com.tui.tui_task.repository;

import java.util.Optional;

import com.tui.tui_task.data.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
	Optional<Order> getOrderByOrderNumberAndClient_Email(String orderNumber, String email);
}
