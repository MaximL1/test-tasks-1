package com.tui.tui_task.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.tui_task.data.dto.request.ClientDto;
import com.tui.tui_task.data.dto.request.PlaceOrderRequest;
import com.tui.tui_task.data.dto.request.UpdateOrderRequest;
import com.tui.tui_task.data.dto.response.OrderResponse;
import com.tui.tui_task.data.dto.response.OrderSearchResponse;
import com.tui.tui_task.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser
	void testCreateOrder() throws Exception {
		PlaceOrderRequest request = new PlaceOrderRequest(
				"1234 Delivery Street",
				10,
				new ClientDto("John", "Doe", "1234567890", "john.doe@example.com")
		);

		OrderResponse response = new OrderResponse(
				"ORD-001", 10, new BigDecimal("49.99"),
				"1234 Delivery Street", "1234567890", Instant.now(), "john.doe@example.com"
		);

		Mockito.when(orderService.createOrder(any())).thenReturn(response);

		mockMvc.perform(post("/orders")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
								.with(csrf()))
			   .andExpect(status().isCreated())
			   .andExpect(jsonPath("$.orderNumber").value("ORD-001"));
	}

	@Test
	@WithMockUser
	void testUpdateOrder() throws Exception {
		UpdateOrderRequest updateRequest = new UpdateOrderRequest(
				"5678 Updated Ave", 15, "john.doe@example.com"
		);

		OrderResponse response = new OrderResponse(
				"ORD-001", 15, new BigDecimal("74.99"),
				"5678 Updated Ave", "1234567890", Instant.now(), "john.doe@example.com"
		);

		Mockito.when(orderService.updateOrder(Mockito.eq("ORD-001"), any())).thenReturn(response);

		mockMvc.perform(put("/orders/ORD-001")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(updateRequest))
								.with(csrf()))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.numberOfPilotes").value(15))
			   .andExpect(jsonPath("$.deliveryAddress").value("5678 Updated Ave"));
	}

	@Test
	@WithMockUser
	void testSearchOrdersByClient() throws Exception {
		OrderSearchResponse searchResponse = new OrderSearchResponse(
				"ORD-123", "Test Street", 5, new BigDecimal("24.99"), Instant.now(), "contact info",
				new ClientDto("Jane", "Smith", "9876543210", "jane.smith@example.com")
		);

		Mockito.when(orderService.searchByClient(any())).thenReturn(List.of(searchResponse));

		mockMvc.perform(get("/orders/search")
								.param("name", "Jane")
								.param("lastName", "Smith")
								.param("email", "jane.smith@example.com")
								.param("deliveryAddress", "Test Street")
								.with(csrf()))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$[0].orderNumber").value("ORD-123"))
			   .andExpect(jsonPath("$[0].clientDto.firstName").value("Jane"));
	}
}
