package com.tui.tui_task.config;

import com.tui.tui_task.data.dto.request.ClientSearchRequest;
import com.tui.tui_task.data.dto.request.PlaceOrderRequest;
import com.tui.tui_task.data.dto.request.UpdateOrderRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OrderLoggingAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderLoggingAspect.class);

	@Pointcut("execution(* com.tui.tui_task..controller.OrderController.*(..))")
	public void orderControllerMethods() {}

	@Before("orderControllerMethods()")
	public void logControllerInputs(JoinPoint joinPoint) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();

		for (Object arg : args) {
			logArgument(className, methodName, arg);
		}
	}

	private void logArgument(String className, String methodName, Object arg) {
		if (arg instanceof PlaceOrderRequest request) {
			logPlaceOrderRequest(className, methodName, request);
		} else if (arg instanceof UpdateOrderRequest request) {
			logUpdateOrderRequest(className, methodName, request);
		} else if (arg instanceof ClientSearchRequest request) {
			logClientSearchRequest(className, methodName, request);
		}
	}

	protected void logPlaceOrderRequest(String className, String methodName, PlaceOrderRequest request) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("{} - {} - PlaceOrderRequest: {}", className, methodName, maskSensitive(request));
		}
	}

	protected void logUpdateOrderRequest(String className, String methodName, UpdateOrderRequest request) {
		LOGGER.info("{} - {} - UpdateOrderRequest[pilotes: {}, deliveryAddress: {}]",
					className, methodName, request.numberOfPilotes(), request.deliveryAddress());
	}

	protected void logClientSearchRequest(String className, String methodName, ClientSearchRequest request) {
		StringBuilder sb = new StringBuilder("SearchRequest[");
		appendIfNotNull(sb, "name", request.name());
		appendIfNotNull(sb, "lastName", request.lastName());
		appendIfNotNull(sb, "email", "***");
		appendIfNotNull(sb, "deliveryAddress", request.deliveryAddress());
		sb.append("]");

		LOGGER.info("{} - {} - {}", className, methodName, sb);
	}

	private void appendIfNotNull(StringBuilder sb, String fieldName, String fieldValue) {
		if (fieldValue != null) {
			if (!sb.isEmpty()) sb.append(", ");
			sb.append(fieldName).append(": ").append(fieldValue);
		}
	}

	private String maskSensitive(PlaceOrderRequest placeOrderRequest) {
		String maskedClient = String.format("Client[firstName=%s, lastName=%s, phoneNumber=***, email=***]",
											placeOrderRequest.clientDto().firstName(),
											placeOrderRequest.clientDto().lastName());

		return String.format("DeliveryAddress=%s, NumberOfPilotes=%d, %s",
							 placeOrderRequest.deliveryAddress(),
							 placeOrderRequest.numberOfPilotes(),
							 maskedClient);
	}
}


