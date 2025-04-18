package com.tui.tui_task.service;

import java.util.List;

import com.tui.tui_task.data.dto.request.ClientSearchRequest;
import com.tui.tui_task.data.dto.request.PlaceOrderRequest;
import com.tui.tui_task.data.dto.request.UpdateOrderRequest;
import com.tui.tui_task.data.dto.response.OrderResponse;
import com.tui.tui_task.data.dto.response.OrderSearchResponse;
import jakarta.validation.Valid;

public interface OrderService {

	OrderResponse createOrder(@Valid PlaceOrderRequest placeOrderRequest);

	OrderResponse updateOrder(String orderNumber, @Valid UpdateOrderRequest updateOrderRequest);

	List<OrderSearchResponse> searchByClient(ClientSearchRequest request);
}
