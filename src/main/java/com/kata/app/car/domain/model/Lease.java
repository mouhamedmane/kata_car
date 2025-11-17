package com.kata.app.car.domain.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Lease {
	private final UUID id;
	private final UUID carId;
	private final UUID customerId;
	private final LocalDate startDate;
	private final LocalDate endDatePlanned;
	private LocalDate returnDate;
	private LeaseStatus status;

	private Lease(UUID id, UUID carId, UUID customerId, LocalDate startDate, LocalDate endDatePlanned, LocalDate returnDate, LeaseStatus status) {
		this.id = Objects.requireNonNull(id, "id must not be null");
		this.carId = Objects.requireNonNull(carId, "carId must not be null");
		this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
		this.startDate = Objects.requireNonNull(startDate, "startDate must not be null");
		this.endDatePlanned = endDatePlanned;
		this.returnDate = returnDate;
		this.status = Objects.requireNonNull(status, "status must not be null");
	}

	public static Lease rehydrate(UUID id, UUID carId, UUID customerId, LocalDate startDate, LocalDate endDatePlanned, LocalDate returnDate, LeaseStatus status) {
		return new Lease(id, carId, customerId, startDate, endDatePlanned, returnDate, status);
	}

	public static Lease start(UUID carId, UUID customerId, LocalDate startDate, LocalDate endDatePlanned) {
		UUID leaseId = UUID.randomUUID();
		LocalDate effectiveStart = startDate != null ? startDate : LocalDate.now();
		return new Lease(leaseId, carId, customerId, effectiveStart, endDatePlanned, null, LeaseStatus.ACTIVE);
	}

	public void returnLease(LocalDate returnDate) {
		if (this.status != LeaseStatus.ACTIVE) {
			throw new IllegalStateException("Lease is not active");
		}
		LocalDate effectiveReturn = returnDate != null ? returnDate : LocalDate.now();
		if (effectiveReturn.isBefore(this.startDate)) {
			throw new IllegalArgumentException("returnDate cannot be before startDate");
		}
		this.returnDate = effectiveReturn;
		this.status = LeaseStatus.RETURNED;
	}

	public UUID getId() {
		return id;
	}

	public UUID getCarId() {
		return carId;
	}

	public UUID getCustomerId() {
		return customerId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDatePlanned() {
		return endDatePlanned;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public LeaseStatus getStatus() {
		return status;
	}
}


