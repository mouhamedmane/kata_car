package com.kata.app.car.infrastructure.jpa.repository;

import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.infrastructure.jpa.entity.CarEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCarRepository extends JpaRepository<CarEntity, UUID> {

	List<CarEntity> findByStatus(CarStatus status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from CarEntity c where c.id = :id")
	Optional<CarEntity> findByIdForUpdate(@Param("id") UUID id);
}


