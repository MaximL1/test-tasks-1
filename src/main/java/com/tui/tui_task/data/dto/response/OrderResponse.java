package com.tui.tui_task.data.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(String orderNumber,
							Integer numberOfPilotes,
							BigDecimal orderTotal,
							String deliveryAddress,
							String phoneNumber,
							Instant createdAt,
							String email) {

}
