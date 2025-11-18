package com.kata.app.car.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Car {
	private final UUID id;
	private final String plateNumber;
	private CarStatus status;

	public Car(UUID id, String plateNumber, CarStatus status) {
		this.id = Objects.requireNonNull(id, "id ne doit pas être nul");
		this.plateNumber = Objects.requireNonNull(plateNumber, "plateNumber ne doit pas être nul");
		this.status = Objects.requireNonNull(status, "status ne doit pas être nul");
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
			throw new IllegalStateException("La voiture est déjà louée");
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


