package com.kata.app.car.infrastructure.jpa.entity;

import com.kata.app.car.domain.model.LeaseStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lease", indexes = {
	@Index(name = "idx_lease_car_status", columnList = "car_id,status")
})
public class LeaseEntity {
	@Id
	private UUID id;

	@Column(name = "car_id", nullable = false)
	private UUID carId;

	@Column(name = "customer_id", nullable = false)
	private UUID customerId;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date_planned")
	private LocalDate endDatePlanned;

	@Column(name = "return_date")
	private LocalDate returnDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private LeaseStatus status;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getCarId() {
		return carId;
	}

	public void setCarId(UUID carId) {
		this.carId = carId;
	}

	public UUID getCustomerId() {
		return customerId;
	}

	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDatePlanned() {
		return endDatePlanned;
	}

	public void setEndDatePlanned(LocalDate endDatePlanned) {
		this.endDatePlanned = endDatePlanned;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public LeaseStatus getStatus() {
		return status;
	}

	public void setStatus(LeaseStatus status) {
		this.status = status;
	}
}


