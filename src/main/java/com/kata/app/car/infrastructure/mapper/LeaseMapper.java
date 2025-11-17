package com.kata.app.car.infrastructure.mapper;

import com.kata.app.car.domain.model.Lease;
import com.kata.app.car.infrastructure.jpa.entity.LeaseEntity;

public final class LeaseMapper {
	private LeaseMapper() {
	}

	public static Lease toDomain(LeaseEntity entity) {
		return Lease.rehydrate(
			entity.getId(),
			entity.getCarId(),
			entity.getCustomerId(),
			entity.getStartDate(),
			entity.getEndDatePlanned(),
			entity.getReturnDate(),
			entity.getStatus()
		);
	}

	public static LeaseEntity toEntity(Lease lease) {
		LeaseEntity entity = new LeaseEntity();
		entity.setId(lease.getId());
		entity.setCarId(lease.getCarId());
		entity.setCustomerId(lease.getCustomerId());
		entity.setStartDate(lease.getStartDate());
		entity.setEndDatePlanned(lease.getEndDatePlanned());
		entity.setReturnDate(lease.getReturnDate());
		entity.setStatus(lease.getStatus());
		return entity;
	}
}


