package com.kata.app.car.infrastructure.mapper;

import com.kata.app.car.domain.model.Car;
import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.infrastructure.jpa.entity.CarEntity;

public final class CarMapper {
	private CarMapper() {
	}

	public static Car toDomain(CarEntity entity) {
		return new Car(
			entity.getId(),
			entity.getPlateNumber(),
			entity.getStatus() != null ? entity.getStatus() : CarStatus.AVAILABLE
		);
	}

	public static CarEntity toEntity(Car car) {
		CarEntity entity = new CarEntity();
		entity.setId(car.getId());
		entity.setPlateNumber(car.getPlateNumber());
		entity.setStatus(car.getStatus());
		return entity;
	}
}


