package com.kata.app.car.infrastructure.jpa.entity;

import com.kata.app.car.domain.model.CarStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "car", uniqueConstraints = {
	@UniqueConstraint(name = "uk_car_plate", columnNames = "plate_number")
})
public class CarEntity {
	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "plate_number", nullable = false)
	private String plateNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private CarStatus status;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public CarStatus getStatus() {
		return status;
	}

	public void setStatus(CarStatus status) {
		this.status = status;
	}
}


