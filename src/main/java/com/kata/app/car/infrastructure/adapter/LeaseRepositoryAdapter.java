package com.kata.app.car.infrastructure.adapter;

import com.kata.app.car.domain.model.Lease;
import com.kata.app.car.domain.model.LeaseStatus;
import com.kata.app.car.domain.repository.LeaseRepository;
import com.kata.app.car.infrastructure.jpa.entity.LeaseEntity;
import com.kata.app.car.infrastructure.jpa.repository.SpringDataLeaseRepository;
import com.kata.app.car.infrastructure.mapper.LeaseMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class LeaseRepositoryAdapter implements LeaseRepository {

	private final SpringDataLeaseRepository springDataLeaseRepository;

	public LeaseRepositoryAdapter(SpringDataLeaseRepository springDataLeaseRepository) {
		this.springDataLeaseRepository = springDataLeaseRepository;
	}

	@Override
	public Optional<Lease> findById(UUID id) {
		return springDataLeaseRepository.findById(id).map(LeaseMapper::toDomain);
	}

	@Override
	public Optional<Lease> findActiveByCarId(UUID carId) {
		return springDataLeaseRepository.findByCarIdAndStatus(carId, LeaseStatus.ACTIVE)
			.map(LeaseMapper::toDomain);
	}

	@Override
	public Lease save(Lease lease) {
		LeaseEntity entity = LeaseMapper.toEntity(lease);
		LeaseEntity saved = springDataLeaseRepository.save(entity);
		return LeaseMapper.toDomain(saved);
	}
}


