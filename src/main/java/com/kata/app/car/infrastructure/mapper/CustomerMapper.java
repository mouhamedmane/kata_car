package com.kata.app.car.infrastructure.mapper;

import com.kata.app.car.domain.model.Customer;
import com.kata.app.car.infrastructure.jpa.entity.CustomerEntity;

public final class CustomerMapper {
	private CustomerMapper() {
	}

	public static Customer toDomain(CustomerEntity entity) {
		return new Customer(entity.getId(), entity.getName());
	}
}


