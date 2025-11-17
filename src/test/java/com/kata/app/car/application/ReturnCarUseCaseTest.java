package com.kata.app.car.application;

import com.kata.app.car.application.exception.LeaseNotActiveException;
import com.kata.app.car.application.exception.LeaseNotFoundException;
import com.kata.app.car.application.model.ReturnCarCommand;
import com.kata.app.car.application.model.ReturnLeaseResponse;
import com.kata.app.car.application.usecase.ReturnCarUseCase;
import com.kata.app.car.domain.model.Car;
import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.domain.model.Lease;
import com.kata.app.car.domain.model.LeaseStatus;
import com.kata.app.car.domain.repository.CarRepository;
import com.kata.app.car.domain.repository.LeaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReturnCarUseCaseTest {

	private LeaseRepository leaseRepository;
	private CarRepository carRepository;
	private ReturnCarUseCase useCase;

	@BeforeEach
	void setUp() {
		leaseRepository = mock(LeaseRepository.class);
		carRepository = mock(CarRepository.class);
		useCase = new ReturnCarUseCase(leaseRepository, carRepository);
	}

	@Test
	void return_success_shouldSetLeaseReturned_andCarAvailable() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		Car car = new Car(carId, "AA-123-AA", CarStatus.AVAILABLE);
		car.markLeased();
		Lease lease = Lease.start(carId, customerId, LocalDate.now().minusDays(1), null);

		when(leaseRepository.findById(lease.getId())).thenReturn(Optional.of(lease));
		when(carRepository.findByIdForUpdate(carId)).thenReturn(Optional.of(car));

		ReturnCarCommand cmd = new ReturnCarCommand();
		cmd.leaseId = lease.getId();
		cmd.returnDate = LocalDate.now();

		ReturnLeaseResponse response = useCase.returnByLeaseId(cmd);

		assertThat(response.status).isEqualTo(LeaseStatus.RETURNED);
		assertThat(car.getStatus()).isEqualTo(CarStatus.AVAILABLE);
	}

	@Test
	void return_shouldFail_whenLeaseNotFound() {
		ReturnCarCommand cmd = new ReturnCarCommand();
		cmd.leaseId = UUID.randomUUID();

		assertThatThrownBy(() -> useCase.returnByLeaseId(cmd))
			.isInstanceOf(LeaseNotFoundException.class);
	}

	@Test
	void return_shouldFail_whenLeaseAlreadyReturned() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		Lease lease = Lease.start(carId, customerId, LocalDate.now().minusDays(3), null);
		lease.returnLease(LocalDate.now().minusDays(1));

		when(leaseRepository.findById(lease.getId())).thenReturn(Optional.of(lease));

		ReturnCarCommand cmd = new ReturnCarCommand();
		cmd.leaseId = lease.getId();

		assertThatThrownBy(() -> useCase.returnByLeaseId(cmd))
			.isInstanceOf(LeaseNotActiveException.class);
	}
}


