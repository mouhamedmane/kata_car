package com.kata.app.car.application.exception;

public class CarAlreadyLeasedException extends RuntimeException {
	public CarAlreadyLeasedException(String message) {
		super(message);
	}
}


