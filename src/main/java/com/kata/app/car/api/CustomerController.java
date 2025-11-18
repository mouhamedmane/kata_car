package com.kata.app.car.api;

import com.kata.app.car.api.dto.CustomerResponse;
import com.kata.app.car.application.exception.CustomerNotFoundException;
import com.kata.app.car.domain.model.Customer;
import com.kata.app.car.domain.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerRepository customerRepository;

	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@GetMapping
	public ResponseEntity<List<CustomerResponse>> listCustomers() {
		List<Customer> customers = customerRepository.findAll();
		List<CustomerResponse> response = customers.stream().map(this::toResponse).toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID customerId) {
		Customer customer = customerRepository.findById(customerId)
			.orElseThrow(() -> new CustomerNotFoundException("Customer %s not found".formatted(customerId)));
		return ResponseEntity.ok(toResponse(customer));
	}

	private CustomerResponse toResponse(Customer customer) {
		CustomerResponse dto = new CustomerResponse();
		dto.id = customer.getId();
		dto.name = customer.getName();
		return dto;
	}
}



