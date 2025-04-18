package com.tui.tui_task.data.dto.response;

import lombok.Getter;

@Getter
public class ApiValidationError {
	private String field;
	private Object rejectedValue;
	private final String message;

	ApiValidationError(String message) {
		this.message = message;
	}

	public ApiValidationError(String field, Object rejectedValue, String message) {
		this.field = field;
		this.rejectedValue = rejectedValue;
		this.message = message;
	}
}
