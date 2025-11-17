package com.kata.app.car.domain.repository;

import com.kata.app.car.domain.model.Lease;

import java.util.Optional;
import java.util.UUID;

public interface LeaseRepository {
	Optional<Lease> findById(UUID id);
	Optional<Lease> findActiveByCarId(UUID carId);
	Lease save(Lease lease);
}


