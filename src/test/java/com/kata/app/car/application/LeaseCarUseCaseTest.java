package com.kata.app.car.application;

import com.kata.app.car.application.exception.CarAlreadyLeasedException;
import com.kata.app.car.application.model.LeaseCarCommand;
import com.kata.app.car.application.model.LeaseResponse;
import com.kata.app.car.application.usecase.LeaseCarUseCase;
import com.kata.app.car.domain.model.Car;
import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.domain.model.Lease;
import com.kata.app.car.domain.repository.CarRepository;
import com.kata.app.car.domain.repository.CustomerRepository;
import com.kata.app.car.domain.repository.LeaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LeaseCarUseCaseTest {

	private CarRepository carRepository;
	private CustomerRepository customerRepository;
	private LeaseRepository leaseRepository;
	private LeaseCarUseCase useCase;

	@BeforeEach
	void setUp() {
		carRepository = mock(CarRepository.class);
		customerRepository = mock(CustomerRepository.class);
		leaseRepository = mock(LeaseRepository.class);
		useCase = new LeaseCarUseCase(carRepository, customerRepository, leaseRepository);
	}

	@Test
	void lease_success_shouldCreateLeaseAndMarkCarLeased() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		Car car = new Car(carId, "AA-123-AA", CarStatus.AVAILABLE);

		when(carRepository.findByIdForUpdate(carId)).thenReturn(Optional.of(car));
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(new com.kata.app.car.domain.model.Customer(customerId, "Alice")));
		when(leaseRepository.findActiveByCarId(carId)).thenReturn(Optional.empty());
		ArgumentCaptor<Lease> leaseCaptor = ArgumentCaptor.forClass(Lease.class);

		LeaseCarCommand cmd = new LeaseCarCommand();
		cmd.carId = carId;
		cmd.customerId = customerId;

		LeaseResponse response = useCase.lease(cmd);

		verify(leaseRepository).save(leaseCaptor.capture());
		verify(carRepository).save(any(Car.class));
		assertThat(response.status).isNotNull();
		assertThat(leaseCaptor.getValue().getCarId()).isEqualTo(carId);
		assertThat(car.getStatus()).isEqualTo(CarStatus.LEASED);
	}

	@Test
	void lease_shouldFail_whenCarAlreadyLeased() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		Car car = new Car(carId, "AA-123-AA", CarStatus.LEASED);

		when(carRepository.findByIdForUpdate(carId)).thenReturn(Optional.of(car));
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(new com.kata.app.car.domain.model.Customer(customerId, "Alice")));
		when(leaseRepository.findActiveByCarId(carId)).thenReturn(Optional.empty());

		LeaseCarCommand cmd = new LeaseCarCommand();
		cmd.carId = carId;
		cmd.customerId = customerId;

		assertThatThrownBy(() -> useCase.lease(cmd))
			.isInstanceOf(CarAlreadyLeasedException.class);
	}

	@Test
	void lease_shouldFail_whenActiveLeaseAlreadyExists() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		Car car = new Car(carId, "AA-123-AA", CarStatus.AVAILABLE);
		Lease existingLease = Lease.start(carId, UUID.randomUUID(), null, null);

		when(carRepository.findByIdForUpdate(carId)).thenReturn(Optional.of(car));
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(new com.kata.app.car.domain.model.Customer(customerId, "Alice")));
		when(leaseRepository.findActiveByCarId(carId)).thenReturn(Optional.of(existingLease));

		LeaseCarCommand cmd = new LeaseCarCommand();
		cmd.carId = carId;
		cmd.customerId = customerId;

		assertThatThrownBy(() -> useCase.lease(cmd))
			.isInstanceOf(CarAlreadyLeasedException.class)
			.hasMessageContaining(existingLease.getId().toString());
	}
}


