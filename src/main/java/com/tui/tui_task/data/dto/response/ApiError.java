package com.tui.tui_task.data.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

@Setter
@Getter
public class ApiError {
	private HttpStatus status;
	private String message;
	private List<ApiValidationError> subErrors;

	public ApiError(HttpStatus httpStatus) {
		this.status = httpStatus;
	}

	public void addValidationErrors(List<FieldError> fieldErrors) {
		fieldErrors.forEach(this::addValidationError);
	}

	private void addValidationError(FieldError fieldError) {
		addSubError(new ApiValidationError(
				fieldError.getField(),
				fieldError.getRejectedValue(),
				fieldError.getDefaultMessage()));
	}

	private void addSubError(ApiValidationError subError) {
		if (subErrors == null) {
			subErrors = new ArrayList<>();
		}
		subErrors.add(subError);
	}
}
