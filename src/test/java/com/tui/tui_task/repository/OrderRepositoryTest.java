package com.tui.tui_task.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import com.tui.tui_task.data.model.Client;
import com.tui.tui_task.data.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Test
	void testGetOrderByOrderNumberAndClientEmail_shouldReturnOrder() {
		Client client = new Client();
		client.setFirstName("John");
		client.setLastName("Doe");
		client.setEmail("john.doe@example.com");
		client.setPhoneNumber("1234567890");
		client = clientRepository.save(client);

		Order order = new Order();
		order.setOrderNumber("ORD-001");
		order.setClient(client);
		order.setDeliveryAddress("1234 Delivery Street");
		order.setNumberOfPilotes(10);
		order.setOrderTotal(new BigDecimal("49.99"));
		order.setCreatedAt(Instant.now());
		orderRepository.save(order);

		Optional<Order> found = orderRepository.getOrderByOrderNumberAndClient_Email("ORD-001", "john.doe@example.com");

		assertTrue(found.isPresent());
		assertEquals("ORD-001", found.get().getOrderNumber());
		assertEquals("john.doe@example.com", found.get().getClient().getEmail());
	}

	@Test
	void testGetOrderByOrderNumberAndClientEmail_shouldReturnEmpty() {
		Optional<Order> found = orderRepository.getOrderByOrderNumberAndClient_Email("ORD-999", "noone@example.com");

		assertFalse(found.isPresent());
	}
}

