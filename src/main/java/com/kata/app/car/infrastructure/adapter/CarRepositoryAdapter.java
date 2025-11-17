package com.kata.app.car.infrastructure.adapter;

import com.kata.app.car.domain.model.Car;
import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.domain.repository.CarRepository;
import com.kata.app.car.infrastructure.jpa.entity.CarEntity;
import com.kata.app.car.infrastructure.jpa.repository.SpringDataCarRepository;
import com.kata.app.car.infrastructure.mapper.CarMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CarRepositoryAdapter implements CarRepository {

	private final SpringDataCarRepository springDataCarRepository;

	public CarRepositoryAdapter(SpringDataCarRepository springDataCarRepository) {
		this.springDataCarRepository = springDataCarRepository;
	}

	@Override
	public Optional<Car> findById(UUID id) {
		return springDataCarRepository.findById(id).map(CarMapper::toDomain);
	}

	@Override
	public Optional<Car> findByIdForUpdate(UUID id) {
		return springDataCarRepository.findByIdForUpdate(id).map(CarMapper::toDomain);
	}

	@Override
	public List<Car> findByStatus(CarStatus status) {
		return springDataCarRepository.findByStatus(status).stream()
			.map(CarMapper::toDomain)
			.toList();
	}

	@Override
	public List<Car> findAll() {
		return springDataCarRepository.findAll().stream()
			.map(CarMapper::toDomain)
			.toList();
	}

	@Override
	public Car save(Car car) {
		CarEntity entity = CarMapper.toEntity(car);
		CarEntity saved = springDataCarRepository.save(entity);
		return CarMapper.toDomain(saved);
	}
}


