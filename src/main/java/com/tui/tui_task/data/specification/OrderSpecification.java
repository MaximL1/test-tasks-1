package com.tui.tui_task.data.specification;

import com.tui.tui_task.data.dto.request.ClientSearchRequest;
import com.tui.tui_task.data.model.Client;
import com.tui.tui_task.data.model.Order;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

	private OrderSpecification() {
		throw new IllegalStateException("Utility class");
	}

	public static Specification<Order> clientMatches(ClientSearchRequest request) {
		return (root, query, cb) -> {
			Join<Order, Client> clientJoin = root.join("client", JoinType.INNER);
			Predicate predicate = cb.conjunction();

			if (request.name() != null && !request.name().isBlank()) {
				predicate = cb.and(predicate,
								   cb.like(cb.lower(clientJoin.get("firstName")), "%" + request.name().toLowerCase() + "%"));
			}

			if (request.lastName() != null && !request.lastName().isBlank()) {
				predicate = cb.and(predicate,
								   cb.like(cb.lower(clientJoin.get("lastName")), "%" + request.lastName().toLowerCase() + "%"));
			}

			if (request.email() != null && !request.email().isBlank()) {
				predicate = cb.and(predicate,
								   cb.like(cb.lower(clientJoin.get("email")), "%" + request.email().toLowerCase() + "%"));
			}

			if (request.deliveryAddress() != null && !request.deliveryAddress().isBlank()) {
				predicate = cb.and(predicate,
								   cb.like(cb.lower(root.get("deliveryAddress")), "%" + request.deliveryAddress().toLowerCase() + "%"));
			}

			return predicate;
		};
	}

}
