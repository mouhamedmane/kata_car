package com.kata.app.car.infrastructure.jpa.repository;

import com.kata.app.car.domain.model.LeaseStatus;
import com.kata.app.car.infrastructure.jpa.entity.LeaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataLeaseRepository extends JpaRepository<LeaseEntity, UUID> {
	Optional<LeaseEntity> findByCarIdAndStatus(UUID carId, LeaseStatus status);
}


