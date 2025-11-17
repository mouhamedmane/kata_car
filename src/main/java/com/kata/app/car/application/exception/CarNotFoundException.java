package com.kata.app.car.application.exception;

public class CarNotFoundException extends RuntimeException {
	public CarNotFoundException(String message) {
		super(message);
	}
}


