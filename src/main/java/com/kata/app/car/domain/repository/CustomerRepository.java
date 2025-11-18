package com.kata.app.car.domain.repository;

import com.kata.app.car.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
	Optional<Customer> findById(UUID id);
	List<Customer> findAll();
}


