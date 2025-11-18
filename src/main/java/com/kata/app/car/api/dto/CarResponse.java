package com.kata.app.car.api.dto;

import com.kata.app.car.domain.model.CarStatus;

import java.util.UUID;

public class CarResponse {
	public UUID id;
	public String plateNumber;
	public CarStatus status;
}



