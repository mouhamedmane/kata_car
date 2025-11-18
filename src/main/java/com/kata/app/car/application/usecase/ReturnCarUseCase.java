package com.kata.app.car.application.usecase;

import com.kata.app.car.application.exception.CarNotFoundException;
import com.kata.app.car.application.exception.LeaseNotActiveException;
import com.kata.app.car.application.exception.LeaseNotFoundException;
import com.kata.app.car.application.model.ReturnCarCommand;
import com.kata.app.car.application.model.ReturnLeaseResponse;
import com.kata.app.car.domain.model.Car;
import com.kata.app.car.domain.model.Lease;
import com.kata.app.car.domain.model.LeaseStatus;
import com.kata.app.car.domain.repository.CarRepository;
import com.kata.app.car.domain.repository.LeaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReturnCarUseCase {

	private final LeaseRepository leaseRepository;
	private final CarRepository carRepository;

	public ReturnCarUseCase(LeaseRepository leaseRepository, CarRepository carRepository) {
		this.leaseRepository = leaseRepository;
		this.carRepository = carRepository;
	}

	public ReturnLeaseResponse returnByLeaseId(ReturnCarCommand command) {
		Lease lease = leaseRepository.findByIdForUpdate(command.leaseId)
			.orElseThrow(() -> new LeaseNotFoundException("Contrat de location %s introuvable".formatted(command.leaseId)));

		if (lease.getStatus() != LeaseStatus.ACTIVE) {
			throw new LeaseNotActiveException("Le contrat de location %s n'est pas actif".formatted(command.leaseId));
		}

		lease.returnLease(command.returnDate);

		Car car = carRepository.findByIdForUpdate(lease.getCarId())
			.orElseThrow(() -> new CarNotFoundException("Voiture %s pour le contrat de location %s introuvable".formatted(lease.getCarId(), lease.getId())));

		car.markAvailable();

		leaseRepository.save(lease);
		carRepository.save(car);

		ReturnLeaseResponse response = new ReturnLeaseResponse();
		response.leaseId = lease.getId();
		response.carId = lease.getCarId();
		response.customerId = lease.getCustomerId();
		response.status = lease.getStatus();
		response.startDate = lease.getStartDate();
		response.returnDate = lease.getReturnDate();
		return response;
	}
}


