package com.kata.app.car.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class LeaseCarRequest {
	@NotNull(message = "l'identifiant de la voiture ne doit pas être nul")
	public UUID carId;
	@NotNull(message = "l'identifiant du client ne doit pas être nul")
	public UUID customerId;
	public LocalDate startDate;
	public LocalDate endDatePlanned;
}


