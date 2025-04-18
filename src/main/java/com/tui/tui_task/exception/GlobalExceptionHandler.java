package com.tui.tui_task.exception;

import com.tui.tui_task.data.dto.response.ApiError;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			@NotNull HttpHeaders headers,
			@NotNull HttpStatusCode status,
			@NotNull WebRequest request) {

		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
		apiError.setMessage("Validation failed");
		apiError.addValidationErrors(ex.getFieldErrors());

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(OrderUpdateNotAllowedException.class)
	protected ResponseEntity<Object> handleOrderUpdateNotAllowedException(OrderUpdateNotAllowedException ex) {
		logger.debug(String.format(ex.getMessage()));
		ApiError apiError = new ApiError(CONFLICT);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFoundExceptionException(EntityNotFoundException ex) {
		logger.debug(String.format(ex.getMessage()));
		ApiError apiError = new ApiError(NOT_FOUND);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(TooManyRequestsException.class)
	protected ResponseEntity<Object> handleEntityNotFoundExceptionException(TooManyRequestsException ex) {
		logger.debug(String.format(ex.getMessage()));
		ApiError apiError = new ApiError(TOO_MANY_REQUESTS);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}
