package com.tui.tui_task.service;

import com.tui.tui_task.data.dto.request.ClientDto;
import com.tui.tui_task.data.dto.request.ClientSearchRequest;
import com.tui.tui_task.data.dto.request.PlaceOrderRequest;
import com.tui.tui_task.data.dto.request.UpdateOrderRequest;
import com.tui.tui_task.data.dto.response.OrderResponse;
import com.tui.tui_task.data.dto.response.OrderSearchResponse;
import com.tui.tui_task.data.mapper.OrderMapper;
import com.tui.tui_task.data.model.Client;
import com.tui.tui_task.data.model.Order;
import com.tui.tui_task.exception.OrderUpdateNotAllowedException;
import com.tui.tui_task.exception.TooManyRequestsException;
import com.tui.tui_task.repository.OrderRepository;
import com.tui.tui_task.service.impl.OrderServiceImpl;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

	private OrderRepository orderRepository;
	private ClientService clientService;
	private OrderMapper orderMapper;

	private OrderServiceImpl orderService;

	@BeforeEach
	void setUp() {
		orderRepository = mock(OrderRepository.class);
		clientService = mock(ClientService.class);
		orderMapper = mock(OrderMapper.class);
		orderService = new OrderServiceImpl(orderRepository, clientService, orderMapper);
	}

	@Test
	void shouldCreateOrderSuccessfully() {
		Client client = new Client();
		client.setDesiredContacts("contacts");

		PlaceOrderRequest request = mock(PlaceOrderRequest.class);
		Order order = new Order();
		order.setNumberOfPilotes(10);
		Order savedOrder = new Order();
		savedOrder.setNumberOfPilotes(10);
		OrderResponse response = new OrderResponse(UUID.randomUUID().toString(),
												   10, BigDecimal.valueOf(13.30),
												   "address", "+2345544667",
												   Instant.now(), "test@mail.com");

		when(request.clientDto()).thenReturn(null);
		when(clientService.getOrCreateClient(null)).thenReturn(client);
		when(orderMapper.dtoToOrder(request)).thenReturn(order);
		when(orderRepository.save(order)).thenReturn(savedOrder);
		when(orderMapper.toDto(savedOrder)).thenReturn(response);

		OrderResponse result = orderService.createOrder(request);

		assertThat(result).isEqualTo(response);
		verify(orderRepository).save(order);
		verify(orderMapper).toDto(savedOrder);
	}

	@Test
	void shouldFailWhenNumberOfPilotesIsNull() {
		Client client = new Client();
		client.setDesiredContacts("contacts");

		PlaceOrderRequest request = mock(PlaceOrderRequest.class);
		Order order = new Order();

		when(request.clientDto()).thenReturn(null);
		when(clientService.getOrCreateClient(null)).thenReturn(client);
		when(orderMapper.dtoToOrder(request)).thenReturn(order);

		assertThatThrownBy(() -> orderService.createOrder(request))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("numberOfPilotes");
	}

	@Test
	void shouldUpdateOrderSuccessfullyWithinTimeLimit() {
		UpdateOrderRequest request = new UpdateOrderRequest("new address", 3, "client@email.com");

		Order order = new Order();
		order.setCreatedAt(Instant.now());
		Client client = new Client();
		client.setFirstName("John");
		client.setLastName("Doe");
		order.setClient(client);

		Order updatedOrder = new Order();
		updatedOrder.setClient(client);
		updatedOrder.setOrderNumber("123");

		OrderResponse expectedResponse = new OrderResponse(
				UUID.randomUUID().toString(),
				3, BigDecimal.valueOf(3.99),
				"new address", "+2345544667",
				Instant.now(), "client@email.com"
		);

		when(orderRepository.getOrderByOrderNumberAndClient_Email("123", request.email()))
				.thenReturn(Optional.of(order));
		when(orderRepository.save(order)).thenReturn(updatedOrder);
		when(orderMapper.toDto(updatedOrder)).thenReturn(expectedResponse);

		OrderResponse result = orderService.updateOrder("123", request);

		assertThat(result).isEqualTo(expectedResponse);
		verify(orderRepository).save(order);
		verify(orderMapper).toDto(updatedOrder);
	}


	@Test
	void shouldThrowEntityNotFound_WhenUpdatingNonExistingOrder() {
		UpdateOrderRequest request = new UpdateOrderRequest("x", 1, "no@email.com");

		when(orderRepository.getOrderByOrderNumberAndClient_Email(any(), any()))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> orderService.updateOrder("id", request))
				.isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void shouldThrowOrderUpdateNotAllowed_WhenTooLateToUpdate() {
		UpdateOrderRequest request = new UpdateOrderRequest("x", 1, "email");
		Order order = new Order();
		order.setCreatedAt(Instant.now().minusSeconds(400));

		when(orderRepository.getOrderByOrderNumberAndClient_Email(any(), any()))
				.thenReturn(Optional.of(order));

		assertThatThrownBy(() -> orderService.updateOrder("id", request))
				.isInstanceOf(OrderUpdateNotAllowedException.class);
	}

	@Test
	void shouldReturnOrdersInSearch() {
		ClientSearchRequest request = mock(ClientSearchRequest.class);
		List<Order> orders = List.of(new Order());
		List<OrderSearchResponse> responseList = List.of(new OrderSearchResponse(UUID.randomUUID().toString(),
																				 "test addres",
																				 10,
																				 BigDecimal.valueOf(13.4),
																				 Instant.now(),
																				 "Desired Contact[\"name\":\"test\", \"phone\":\"+22222222\"]",
																				 new ClientDto("test_name", "test_last_name", "+11111111111", "test@email.com")));

		when(orderRepository.findAll((Specification<Order>) any())).thenReturn(orders);
		when(orderMapper.toSearchResponse(orders)).thenReturn(responseList);

		List<OrderSearchResponse> result = orderService.searchByClient(request);

		assertThat(result).isEqualTo(responseList);
	}

	@Test
	void shouldThrowTooManyRequestsException_OnRateLimitFallback() {
		RequestNotPermitted ex = mock(RequestNotPermitted.class);

		UpdateOrderRequest updateRequest = new UpdateOrderRequest("some address", 2, "test@mail.com");

		assertThatThrownBy(() -> orderService.rateLimitFallback("order123", updateRequest, ex))
				.isInstanceOf(TooManyRequestsException.class)
				.hasMessageContaining("Rate limit exceeded");
	}

	@Test
	void shouldThrowRuntimeException_WhenMapperFails() {
		PlaceOrderRequest request = mock(PlaceOrderRequest.class);

		when(request.clientDto()).thenReturn(null);
		when(clientService.getOrCreateClient(null)).thenReturn(new Client());
		when(orderMapper.dtoToOrder(request)).thenThrow(new RuntimeException("Mapping failed"));

		assertThatThrownBy(() -> orderService.createOrder(request))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Mapping failed");
	}

	@Test
	void shouldThrowRuntimeException_WhenSavingOrderFails() {
		PlaceOrderRequest request = mock(PlaceOrderRequest.class);
		Order order = new Order();
		order.setNumberOfPilotes(3);
		order.setDeliveryAddress("valid address");

		when(request.clientDto()).thenReturn(null);
		when(clientService.getOrCreateClient(null)).thenReturn(new Client());
		when(orderMapper.dtoToOrder(request)).thenReturn(order);
		when(orderRepository.save(order)).thenThrow(new RuntimeException("DB error"));

		assertThatThrownBy(() -> orderService.createOrder(request))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("DB error");
	}
}
