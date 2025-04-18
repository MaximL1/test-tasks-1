package com.tui.tui_task.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import com.tui.tui_task.data.dto.request.ClientSearchRequest;
import com.tui.tui_task.data.dto.request.PlaceOrderRequest;
import com.tui.tui_task.data.dto.request.UpdateOrderRequest;
import com.tui.tui_task.data.dto.response.OrderResponse;
import com.tui.tui_task.data.dto.response.OrderSearchResponse;
import com.tui.tui_task.data.mapper.OrderMapper;
import com.tui.tui_task.data.model.Client;
import com.tui.tui_task.data.model.Order;
import com.tui.tui_task.data.specification.OrderSpecification;
import com.tui.tui_task.exception.OrderUpdateNotAllowedException;
import com.tui.tui_task.exception.TooManyRequestsException;
import com.tui.tui_task.repository.OrderRepository;
import com.tui.tui_task.service.ClientService;
import com.tui.tui_task.service.OrderService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
	private static final BigDecimal PRICE = BigDecimal.valueOf(1.33);

	private final OrderRepository orderRepository;
	private final ClientService clientService;
	private final OrderMapper orderMapper;

	@Override
	@Transactional
	public OrderResponse createOrder(PlaceOrderRequest placeOrderRequest) {
		Client client = clientService.getOrCreateClient(placeOrderRequest.clientDto());
		Order order = orderMapper.dtoToOrder(placeOrderRequest);
		order.setClient(client);
		order.setDesiredContacts(client.getDesiredContacts());
		order.setOrderTotal(calculateTotal(order.getNumberOfPilotes()));
		order.setCreatedAt(Instant.now());
		order.setOrderNumber(generateOrderNumber());
		LOGGER.info("{} - Created order with number: {}, for: {}",
					this.getClass().getSimpleName(),
					order.getOrderNumber(),
					order.getClient().getFirstName());
		return orderMapper.toDto(orderRepository.save(order));
	}

	@Override
	@RateLimiter(name = "orderUpdateRateLimiter", fallbackMethod = "rateLimitFallback")
	public OrderResponse updateOrder(String orderNumber, UpdateOrderRequest updateOrderRequest) {
		return orderRepository
				.getOrderByOrderNumberAndClient_Email(orderNumber, updateOrderRequest.email())
				.map(existingOrder -> {
					checkPossibilityOfUpdate(existingOrder.getCreatedAt());

					existingOrder.setDeliveryAddress(updateOrderRequest.deliveryAddress());
					existingOrder.setNumberOfPilotes(updateOrderRequest.numberOfPilotes());
					existingOrder.setOrderTotal(calculateTotal(updateOrderRequest.numberOfPilotes()));

					Order updatedOrder = orderRepository.save(existingOrder);
					LOGGER.info("{} - Updated order: {}, for: {} {}",
								this.getClass().getSimpleName(),
								updatedOrder.getOrderNumber(),
								updatedOrder.getClient().getFirstName(),
								updatedOrder.getClient().getLastName());

					return orderMapper.toDto(updatedOrder);
				})
				.orElseThrow(() -> new EntityNotFoundException("Order not found with number: " +
					orderNumber + " and client email: " + updateOrderRequest.email()));
	}

	@Override
	public List<OrderSearchResponse> searchByClient(ClientSearchRequest request) {
		List <Order> orders = orderRepository.findAll(OrderSpecification.clientMatches(request));
		return orderMapper.toSearchResponse(orders);
	}

	private void checkPossibilityOfUpdate(Instant createdAt) {
		Instant now = Instant.now();
		if (createdAt.plus(5, ChronoUnit.MINUTES).isBefore(now)) {
			throw new OrderUpdateNotAllowedException("Order can only be updated within 5 minutes of creation.");
		}
	}

	private BigDecimal calculateTotal(@NotNull(message = "Number of Pilotes cannot be null") Integer numberOfPilotes) {
		return PRICE.multiply(BigDecimal.valueOf(numberOfPilotes));
	}

	private String generateOrderNumber() {
		return UUID.randomUUID().toString();
	}

	public void rateLimitFallback(String orderNumber, UpdateOrderRequest updateOrderRequest, RequestNotPermitted ex) {
		throw new TooManyRequestsException("Rate limit exceeded. Please try again later.");
	}
}
