package com.tui.tui_task.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(min = 2, max = 40, message = "FirstName must be between 2 and 40 characters")
	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Size(min = 2, max = 40, message = "LastName must be between 2 and 40 characters")
	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Pattern(regexp = "^[0-9\\-+]{9,15}$", message = "Invalid telephone format")
	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Email(message = "Email should be valid")
	@Size(max = 50, message = "Email must be no longer than 50 characters")
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	//for real application is used json structure.
	@Transient
	private String desiredContacts;
}
