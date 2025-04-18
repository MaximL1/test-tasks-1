package com.tui.tui_task.data.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.tui.tui_task.utils.ValueIn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Order number cannot be null")
	@Size(max = 36, message = "Order number cannot be longer than 36 characters")
	@Column(name = "order_number", nullable = false, unique = true)
	private String orderNumber;

	@Column(name = "delivery_address", nullable = false)
	@NotBlank(message = "Delivery deliveryAddress is required")
	@Size(min = 10, max = 120, message = "deliveryAddress must be between 10 and 120 characters")
	private String deliveryAddress;

	@NotNull(message = "Number of Pilotes cannot be null")
	@ValueIn(value = {5, 10, 15}, message = "pilotes must be 5, 10 or 15")
	@Column(name = "number_of_pilotes")
	private Integer numberOfPilotes;

	@Column(name = "order_total", nullable = false)
	private BigDecimal orderTotal;

	//Considering use auditing functionality for full implementation
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;

	@Column(name = "desired_contacts")
	private String desiredContacts;
}
