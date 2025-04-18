package com.tui.tui_task.data.dto.request;

import com.tui.tui_task.utils.ValueIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlaceOrderRequest(
		@NotBlank(message = "deliveryAddress  is required")
		@Size(min = 10, max = 120, message = "deliveryAddress must be between 10 and 120 characters")
		String deliveryAddress,

		@NotNull(message = "Number of Pilotes cannot be null")
		@ValueIn(value = {5, 10, 15}, message = "pilotes must be 5, 10 or 15")
		int numberOfPilotes,

		@Valid
		ClientDto clientDto
) {}
