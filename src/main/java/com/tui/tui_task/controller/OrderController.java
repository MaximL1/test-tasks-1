package com.tui.tui_task.controller;

import java.util.List;

import com.tui.tui_task.data.dto.request.ClientSearchRequest;
import com.tui.tui_task.data.dto.request.PlaceOrderRequest;
import com.tui.tui_task.data.dto.request.UpdateOrderRequest;
import com.tui.tui_task.data.dto.response.OrderResponse;
import com.tui.tui_task.data.dto.response.OrderSearchResponse;
import com.tui.tui_task.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Manage Orders")
@RequiredArgsConstructor
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

	private final OrderService orderService;

	@Operation(summary = "Place order", description = "Create an order for pilotes")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "201", description = "Order is placed successfully"),
			@ApiResponse(responseCode = "400",
					description = "Request input is not proper, input type mismatch or missing required parameter")
		}
	)
	@PostMapping
	public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest placeOrderRequest) {
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(orderService.createOrder(placeOrderRequest));
	}

	@Operation(summary = "Place order", description = "Create an order for pilotes")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "201", description = "Order is placed successfully"),
			@ApiResponse(responseCode = "400",
				description = "Request input is not proper, input type mismatch or missing required parameter")
		}
	)
	@PutMapping("/{orderNumber}")
	public OrderResponse updateOrder(@PathVariable String orderNumber,
									 @RequestBody @Valid UpdateOrderRequest updateOrderRequest) {
		return orderService.updateOrder(orderNumber, updateOrderRequest);
	}

	@Operation(
			summary = "Search orders by client",
			description = "Performs a partial case-insensitive search for orders based on client's name, last name, and deliveryAddress.",
			security = @SecurityRequirement(name = "bearerAuth")
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful search"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	@GetMapping("/search")
	public ResponseEntity<List<OrderSearchResponse>> searchOrdersByClient(@ParameterObject ClientSearchRequest request) {
		return ResponseEntity.ok(orderService.searchByClient(request));
	}
}
