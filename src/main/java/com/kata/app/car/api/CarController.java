package com.kata.app.car.api;

import com.kata.app.car.api.dto.CarResponse;
import com.kata.app.car.application.exception.CarNotFoundException;
import com.kata.app.car.domain.model.Car;
import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.domain.repository.CarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cars")
public class CarController {

	private final CarRepository carRepository;

	public CarController(CarRepository carRepository) {
		this.carRepository = carRepository;
	}

	@GetMapping
	public ResponseEntity<List<CarResponse>> listCars(@RequestParam(name = "status", required = false) CarStatus status) {
		List<Car> cars = (status != null) ? carRepository.findByStatus(status) : carRepository.findAll();
		List<CarResponse> response = cars.stream().map(this::toResponse).toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{carId}")
	public ResponseEntity<CarResponse> getCar(@PathVariable UUID carId) {
		Car car = carRepository.findById(carId)
			.orElseThrow(() -> new CarNotFoundException("Car %s not found".formatted(carId)));
		return ResponseEntity.ok(toResponse(car));
	}

	private CarResponse toResponse(Car car) {
		CarResponse dto = new CarResponse();
		dto.id = car.getId();
		dto.plateNumber = car.getPlateNumber();
		dto.status = car.getStatus();
		return dto;
	}
}



