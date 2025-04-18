package com.tui.tui_task.data.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClientDto(
		@NotBlank(message = "First name is required")
		@Size(min = 2, max = 40, message = "First name must be between 2 and 40 characters")
		String firstName,

		@NotBlank(message = "Last name is required")
		@Size(min = 2, max = 40, message = "Last name must be between 2 and 40 characters")
		String lastName,

		@NotBlank(message = "Phone number is required")
		@Pattern(regexp = "^[0-9\\-+]{9,15}$", message = "Invalid phone number format")
		String phoneNumber,

		@NotBlank(message = "Email is required")
		@Email(message = "Email should be valid")
		@Size(max = 50, message = "Email must be no longer than 50 characters")
		String email
) {}
