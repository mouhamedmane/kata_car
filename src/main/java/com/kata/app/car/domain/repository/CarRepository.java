package com.kata.app.car.domain.repository;

import com.kata.app.car.domain.model.Car;
import com.kata.app.car.domain.model.CarStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarRepository {
	Optional<Car> findById(UUID id);
	List<Car> findByStatus(CarStatus status);
	List<Car> findAll();
	Car save(Car car);

	default Optional<Car> findByIdForUpdate(UUID id) {
		return findById(id);
	}
}


