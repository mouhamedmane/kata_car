package com.kata.app.car.api.dto;

import com.kata.app.car.domain.model.LeaseStatus;

import java.time.LocalDate;
import java.util.UUID;

public class LeaseDetailsResponse {
	public UUID leaseId;
	public UUID carId;
	public UUID customerId;
	public LeaseStatus status;
	public LocalDate startDate;
	public LocalDate endDatePlanned;
	public LocalDate returnDate;
}



