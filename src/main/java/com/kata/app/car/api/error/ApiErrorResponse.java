package com.kata.app.car.api.error;

public class ApiErrorResponse {
	public String error;
	public String message;

	public ApiErrorResponse(String error, String message) {
		this.error = error;
		this.message = message;
	}
}


