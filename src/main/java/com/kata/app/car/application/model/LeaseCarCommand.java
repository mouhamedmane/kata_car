package com.kata.app.car.application.model;

import java.time.LocalDate;
import java.util.UUID;

public class LeaseCarCommand {
	public UUID carId;
	public UUID customerId;
	public LocalDate startDate;
	public LocalDate endDatePlanned;
}


