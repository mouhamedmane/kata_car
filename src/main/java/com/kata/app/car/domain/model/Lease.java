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
		this.id = Objects.requireNonNull(id, "id ne doit pas être nul");
		this.carId = Objects.requireNonNull(carId, "carId ne doit pas être nul");
		this.customerId = Objects.requireNonNull(customerId, "customerId ne doit pas être nul");
		this.startDate = Objects.requireNonNull(startDate, "startDate ne doit pas être nul");
		this.endDatePlanned = endDatePlanned;
		this.returnDate = returnDate;
		this.status = Objects.requireNonNull(status, "status ne doit pas être nul");
	}

	public static Lease rehydrate(UUID id, UUID carId, UUID customerId, LocalDate startDate, LocalDate endDatePlanned, LocalDate returnDate, LeaseStatus status) {
		return new Lease(id, carId, customerId, startDate, endDatePlanned, returnDate, status);
	}

	public static Lease start(UUID carId, UUID customerId, LocalDate startDate, LocalDate endDatePlanned) {
		UUID leaseId = UUID.randomUUID();
		LocalDate effectiveStart = startDate != null ? startDate : LocalDate.now();
		if (endDatePlanned != null && endDatePlanned.isBefore(effectiveStart)) {
			throw new IllegalArgumentException("endDatePlanned ne peut pas être antérieure à startDate");
		}
		return new Lease(leaseId, carId, customerId, effectiveStart, endDatePlanned, null, LeaseStatus.ACTIVE);
	}

	public void returnLease(LocalDate returnDate) {
		if (this.status != LeaseStatus.ACTIVE) {
			throw new IllegalStateException("La location n'est pas active");
		}
		LocalDate effectiveReturn = returnDate != null ? returnDate : LocalDate.now();
		if (effectiveReturn.isBefore(this.startDate)) {
			throw new IllegalArgumentException("returnDate ne peut pas être antérieure à startDate");
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


