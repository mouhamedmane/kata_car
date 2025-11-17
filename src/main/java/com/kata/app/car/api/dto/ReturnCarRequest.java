package com.kata.app.car.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReturnCarRequest {
	@NotNull
	public LocalDate returnDate;
}


