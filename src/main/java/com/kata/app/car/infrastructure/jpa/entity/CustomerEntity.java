package com.kata.app.car.infrastructure.jpa.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "customer")
public class CustomerEntity {
	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "name", nullable = false)
	private String name;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}


