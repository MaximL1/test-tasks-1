package com.tui.tui_task.data.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.tui.tui_task.data.dto.request.ClientDto;

public record OrderSearchResponse(
		String orderNumber,
		String deliveryAddress,
		Integer numberOfPilotes,
		BigDecimal orderTotal,
		Instant createdAt,
		String desiredContacts,
		ClientDto clientDto
) {}
