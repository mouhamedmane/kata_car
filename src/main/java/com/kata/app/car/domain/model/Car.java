package com.kata.app.car.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Car {
	private final UUID id;
	private final String plateNumber;
	private CarStatus status;

	public Car(UUID id, String plateNumber, CarStatus status) {
		this.id = Objects.requireNonNull(id, "id must not be null");
		this.plateNumber = Objects.requireNonNull(plateNumber, "plateNumber must not be null");
		this.status = Objects.requireNonNull(status, "status must not be null");
	}

	public UUID getId() {
		return id;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public CarStatus getStatus() {
		return status;
	}

	public void markLeased() {
		if (this.status == CarStatus.LEASED) {
			throw new IllegalStateException("Car is already leased");
		}
		this.status = CarStatus.LEASED;
	}

	public void markAvailable() {
		if (this.status == CarStatus.AVAILABLE) {
			return;
		}
		this.status = CarStatus.AVAILABLE;
	}
}


