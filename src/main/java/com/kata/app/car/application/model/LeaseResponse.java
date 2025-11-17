package com.kata.app.car.application.model;

import com.kata.app.car.domain.model.LeaseStatus;

import java.time.LocalDate;
import java.util.UUID;

public class LeaseResponse {
	public UUID leaseId;
	public UUID carId;
	public UUID customerId;
	public LeaseStatus status;
	public LocalDate startDate;
}


