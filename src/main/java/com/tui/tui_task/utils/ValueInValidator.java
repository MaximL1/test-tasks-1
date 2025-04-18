package com.tui.tui_task.utils;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public class ValueInValidator implements ConstraintValidator<ValueIn, Integer> {
	private List<Integer> values;
	private String message;

	@Override
	public void initialize(ValueIn constraintAnnotation) {
		int[] oneOf = constraintAnnotation.value();
		this.values = Arrays.stream(oneOf).boxed().toList();
		this.message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(Integer integer, ConstraintValidatorContext context) {
		if (integer == null) {
			return true;
		}

		boolean isValid = values.contains(integer);
		if (!isValid) {
			HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
			hibernateContext.disableDefaultConstraintViolation();
			hibernateContext.buildConstraintViolationWithTemplate(message)
							.addConstraintViolation();
		}
		return isValid;
	}
}
