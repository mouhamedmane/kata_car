package com.kata.app.car.application.exception;

public class LeaseNotActiveException extends RuntimeException {
	public LeaseNotActiveException(String message) {
		super(message);
	}
}

