package com.kata.app.car.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.app.car.api.dto.LeaseCarRequest;
import com.kata.app.car.api.dto.ReturnCarRequest;
import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.infrastructure.jpa.entity.CarEntity;
import com.kata.app.car.infrastructure.jpa.entity.CustomerEntity;
import com.kata.app.car.infrastructure.jpa.repository.SpringDataCarRepository;
import com.kata.app.car.infrastructure.jpa.repository.SpringDataCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LeaseControllerIT {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private SpringDataCarRepository carRepository;
	@Autowired
	private SpringDataCustomerRepository customerRepository;

	private UUID availableCarId;
	private UUID anyCustomerId;

	@BeforeEach
	void setUp() {
		CarEntity car = carRepository.findAll().stream()
			.filter(c -> c.getStatus() == CarStatus.AVAILABLE)
			.findFirst()
			.orElseThrow();
		availableCarId = car.getId();
		CustomerEntity customer = customerRepository.findAll().stream().findFirst().orElseThrow();
		anyCustomerId = customer.getId();
	}

	@Test
	void lease_then_conflict_on_second_lease_and_then_return_success() throws Exception {
		LeaseCarRequest leaseReq = new LeaseCarRequest();
		leaseReq.carId = availableCarId;
		leaseReq.customerId = anyCustomerId;

		MvcResult leaseResult = mockMvc.perform(
				post("/api/leases")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(leaseReq))
			)
			.andExpect(status().isCreated())
			.andReturn();

		String leaseJson = leaseResult.getResponse().getContentAsString();
		UUID leaseId = UUID.fromString(objectMapper.readTree(leaseJson).get("leaseId").asText());
		assertThat(leaseId).isNotNull();

		mockMvc.perform(
				post("/api/leases")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(leaseReq))
			)
			.andExpect(status().isConflict());

		ReturnCarRequest returnReq = new ReturnCarRequest();
		returnReq.returnDate = LocalDate.now();
		mockMvc.perform(
				post("/api/leases/{leaseId}/return", leaseId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(returnReq))
			)
			.andExpect(status().isOk());
	}

	@Test
	void return_not_found_for_unknown_lease() throws Exception {
		ReturnCarRequest returnReq = new ReturnCarRequest();
		returnReq.returnDate = LocalDate.now();
		mockMvc.perform(
				post("/api/leases/{leaseId}/return", UUID.randomUUID())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(returnReq))
			)
			.andExpect(status().isNotFound());
	}
}


