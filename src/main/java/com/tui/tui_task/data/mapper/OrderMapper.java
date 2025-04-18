package com.tui.tui_task.data.mapper;

import java.util.List;

import com.tui.tui_task.data.dto.request.PlaceOrderRequest;
import com.tui.tui_task.data.dto.response.OrderResponse;
import com.tui.tui_task.data.dto.response.OrderSearchResponse;
import com.tui.tui_task.data.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ClientMapper.class)
public interface OrderMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "orderNumber", ignore = true)
	@Mapping(target = "orderTotal", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "client", ignore = true)
	@Mapping(target = "desiredContacts", ignore = true)
	Order dtoToOrder(PlaceOrderRequest placeOrderRequest);

	@Mapping(target = "phoneNumber", source = "client.phoneNumber")
	@Mapping(target = "email", source = "client.email")
	OrderResponse toDto(Order order);

	@Mapping(target = "clientDto", source = "client")
	List<OrderSearchResponse> toSearchResponse(List<Order> orders);

	@Mapping(target = "clientDto", source = "client")
	OrderSearchResponse toSearchResponse(Order order);
}
