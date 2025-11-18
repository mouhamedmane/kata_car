package com.kata.app.car.api;

import com.kata.app.car.api.dto.LeaseCarRequest;
import com.kata.app.car.api.dto.LeaseCarResponse;
import com.kata.app.car.api.dto.LeaseDetailsResponse;
import com.kata.app.car.api.dto.ReturnCarRequest;
import com.kata.app.car.api.dto.ReturnCarResponse;
import com.kata.app.car.application.exception.LeaseNotFoundException;
import com.kata.app.car.application.model.LeaseCarCommand;
import com.kata.app.car.application.model.LeaseResponse;
import com.kata.app.car.application.model.ReturnCarCommand;
import com.kata.app.car.application.model.ReturnLeaseResponse;
import com.kata.app.car.application.usecase.LeaseCarUseCase;
import com.kata.app.car.application.usecase.ReturnCarUseCase;
import com.kata.app.car.domain.model.Lease;
import com.kata.app.car.domain.repository.LeaseRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/leases")
public class LeaseController {

	private final LeaseCarUseCase leaseCarUseCase;
	private final ReturnCarUseCase returnCarUseCase;
	private final LeaseRepository leaseRepository;

	public LeaseController(LeaseCarUseCase leaseCarUseCase, ReturnCarUseCase returnCarUseCase, LeaseRepository leaseRepository) {
		this.leaseCarUseCase = leaseCarUseCase;
		this.returnCarUseCase = returnCarUseCase;
		this.leaseRepository = leaseRepository;
	}

	@PostMapping
	public ResponseEntity<LeaseCarResponse> leaseCar(@Valid @RequestBody LeaseCarRequest request) {
		LeaseCarCommand cmd = new LeaseCarCommand();
		cmd.carId = request.carId;
		cmd.customerId = request.customerId;
		cmd.startDate = request.startDate;
		cmd.endDatePlanned = request.endDatePlanned;

		LeaseResponse response = leaseCarUseCase.lease(cmd);
		LeaseCarResponse api = new LeaseCarResponse();
		api.leaseId = response.leaseId;
		api.carId = response.carId;
		api.customerId = response.customerId;
		api.status = response.status;
		api.startDate = response.startDate;
		return ResponseEntity.status(HttpStatus.CREATED)
			.header("Location", "/api/leases/" + api.leaseId)
			.body(api);
	}

	@GetMapping("/{leaseId}")
	public ResponseEntity<LeaseDetailsResponse> getLease(@PathVariable UUID leaseId) {
		Lease lease = leaseRepository.findById(leaseId)
			.orElseThrow(() -> new LeaseNotFoundException("Lease %s not found".formatted(leaseId)));
		LeaseDetailsResponse api = new LeaseDetailsResponse();
		api.leaseId = lease.getId();
		api.carId = lease.getCarId();
		api.customerId = lease.getCustomerId();
		api.status = lease.getStatus();
		api.startDate = lease.getStartDate();
		api.endDatePlanned = lease.getEndDatePlanned();
		api.returnDate = lease.getReturnDate();
		return ResponseEntity.ok(api);
	}

	@PostMapping("/{leaseId}/return")
	public ResponseEntity<ReturnCarResponse> returnCar(@PathVariable UUID leaseId, @Valid @RequestBody ReturnCarRequest request) {
		ReturnCarCommand cmd = new ReturnCarCommand();
		cmd.leaseId = leaseId;
		cmd.returnDate = request.returnDate;
		ReturnLeaseResponse response = returnCarUseCase.returnByLeaseId(cmd);
		ReturnCarResponse api = new ReturnCarResponse();
		api.leaseId = response.leaseId;
		api.carId = response.carId;
		api.customerId = response.customerId;
		api.status = response.status;
		api.startDate = response.startDate;
		api.returnDate = response.returnDate;
		return ResponseEntity.ok(api);
	}
}


