package com.kata.app.car.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Customer {
	private final UUID id;
	private final String name;

	public Customer(UUID id, String name) {
		this.id = Objects.requireNonNull(id, "id ne doit pas être nul");
		this.name = Objects.requireNonNull(name, "name ne doit pas être nul");
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}


