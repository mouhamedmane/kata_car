package com.kata.app.car.domain;

import com.kata.app.car.domain.model.Lease;
import com.kata.app.car.domain.model.LeaseStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeaseTest {

	@Test
	void start_shouldCreateActiveLease_withStartDateDefaultedWhenNull() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();

		Lease lease = Lease.start(carId, customerId, null, null);

		assertThat(lease.getStatus()).isEqualTo(LeaseStatus.ACTIVE);
		assertThat(lease.getStartDate()).isNotNull();
		assertThat(lease.getCarId()).isEqualTo(carId);
		assertThat(lease.getCustomerId()).isEqualTo(customerId);
	}

	@Test
	void start_shouldFail_whenEndDatePlannedBeforeProvidedStartDate() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		LocalDate start = LocalDate.now();
		LocalDate endDatePlanned = start.minusDays(1);

		assertThatThrownBy(() -> Lease.start(carId, customerId, start, endDatePlanned))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("endDatePlanned");
	}

	@Test
	void start_shouldFail_whenEndDatePlannedBeforeTodayAndStartDateOmitted() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		LocalDate endDatePlanned = LocalDate.now().minusDays(1);

		assertThatThrownBy(() -> Lease.start(carId, customerId, null, endDatePlanned))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("endDatePlanned");
	}

	@Test
	void returnLease_shouldSetStatusReturned_andSetReturnDate() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		LocalDate start = LocalDate.now().minusDays(1);
		LocalDate ret = LocalDate.now();

		Lease lease = Lease.start(carId, customerId, start, null);
		lease.returnLease(ret);

		assertThat(lease.getStatus()).isEqualTo(LeaseStatus.RETURNED);
		assertThat(lease.getReturnDate()).isEqualTo(ret);
	}

	@Test
	void returnLease_shouldFail_whenReturnDateBeforeStartDate() {
		UUID carId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		LocalDate start = LocalDate.now();
		LocalDate ret = start.minusDays(1);

		Lease lease = Lease.start(carId, customerId, start, null);

		assertThatThrownBy(() -> lease.returnLease(ret))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("before startDate");
	}
}


