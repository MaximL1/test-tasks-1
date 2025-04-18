package com.tui.tui_task.data.dto.request;

public record ClientSearchRequest(String name,
								  String lastName,
								  String email,
								  String deliveryAddress) {
}
