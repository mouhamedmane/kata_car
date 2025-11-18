package com.kata.app.car.application.usecase;

import com.kata.app.car.application.exception.CarAlreadyLeasedException;
import com.kata.app.car.application.exception.CarNotFoundException;
import com.kata.app.car.application.exception.CustomerNotFoundException;
import com.kata.app.car.application.model.LeaseCarCommand;
import com.kata.app.car.application.model.LeaseResponse;
import com.kata.app.car.domain.model.Car;
import com.kata.app.car.domain.model.CarStatus;
import com.kata.app.car.domain.model.Lease;
import com.kata.app.car.domain.repository.CarRepository;
import com.kata.app.car.domain.repository.CustomerRepository;
import com.kata.app.car.domain.repository.LeaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LeaseCarUseCase {

	private final CarRepository carRepository;
	private final CustomerRepository customerRepository;
	private final LeaseRepository leaseRepository;

	public LeaseCarUseCase(CarRepository carRepository,
					   CustomerRepository customerRepository,
					   LeaseRepository leaseRepository) {
		this.carRepository = carRepository;
		this.customerRepository = customerRepository;
		this.leaseRepository = leaseRepository;
	}

	public LeaseResponse lease(LeaseCarCommand command) {
		Car car = carRepository.findByIdForUpdate(command.carId)
			.orElseThrow(() -> new CarNotFoundException("Voiture %s introuvable".formatted(command.carId)));

		customerRepository.findById(command.customerId)
			.orElseThrow(() -> new CustomerNotFoundException("Client %s introuvable".formatted(command.customerId)));

		leaseRepository.findActiveByCarId(car.getId())
			.ifPresent(existing -> {
				throw new CarAlreadyLeasedException("La voiture %s a déjà un contrat de location actif %s".formatted(car.getId(), existing.getId()));
			});

		if (car.getStatus() != CarStatus.AVAILABLE) {
			throw new CarAlreadyLeasedException("La voiture %s est déjà louée".formatted(car.getId()));
		}

		Lease lease = Lease.start(car.getId(), command.customerId, command.startDate, command.endDatePlanned);
		car.markLeased();

		leaseRepository.save(lease);
		carRepository.save(car);

		LeaseResponse response = new LeaseResponse();
		response.leaseId = lease.getId();
		response.carId = lease.getCarId();
		response.customerId = lease.getCustomerId();
		response.status = lease.getStatus();
		response.startDate = lease.getStartDate();
		return response;
	}
}


