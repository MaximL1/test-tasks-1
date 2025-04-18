package com.tui.tui_task.exception;

public class OrderUpdateNotAllowedException extends RuntimeException {
	public OrderUpdateNotAllowedException(String message) {
		super(message);
	}
}
