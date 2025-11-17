package com.kata.app.car.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Customer {
	private final UUID id;
	private final String name;

	public Customer(UUID id, String name) {
		this.id = Objects.requireNonNull(id, "id must not be null");
		this.name = Objects.requireNonNull(name, "name must not be null");
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}


