package com.kata.app.car.infrastructure.jpa.repository;

import com.kata.app.car.domain.model.LeaseStatus;
import com.kata.app.car.infrastructure.jpa.entity.LeaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataLeaseRepository extends JpaRepository<LeaseEntity, UUID> {
	Optional<LeaseEntity> findByCarIdAndStatus(UUID carId, LeaseStatus status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select l from LeaseEntity l where l.id = :id")
	Optional<LeaseEntity> findByIdForUpdate(@Param("id") UUID id);
}


