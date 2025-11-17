package com.kata.app.car.infrastructure.adapter;

import com.kata.app.car.domain.model.Customer;
import com.kata.app.car.domain.repository.CustomerRepository;
import com.kata.app.car.infrastructure.jpa.repository.SpringDataCustomerRepository;
import com.kata.app.car.infrastructure.mapper.CustomerMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

	private final SpringDataCustomerRepository springDataCustomerRepository;

	public CustomerRepositoryAdapter(SpringDataCustomerRepository springDataCustomerRepository) {
		this.springDataCustomerRepository = springDataCustomerRepository;
	}

	@Override
	public Optional<Customer> findById(UUID id) {
		return springDataCustomerRepository.findById(id).map(CustomerMapper::toDomain);
	}
}


