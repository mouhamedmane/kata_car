package com.kata.app.car.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class LeaseCarRequest {
	@NotNull
	public UUID carId;
	@NotNull
	public UUID customerId;
	public LocalDate startDate;
	public LocalDate endDatePlanned;
}


