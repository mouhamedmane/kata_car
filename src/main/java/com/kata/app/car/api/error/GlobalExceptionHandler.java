package com.kata.app.car.api.error;

import com.kata.app.car.application.exception.CarAlreadyLeasedException;
import com.kata.app.car.application.exception.CarNotFoundException;
import com.kata.app.car.application.exception.CustomerNotFoundException;
import com.kata.app.car.application.exception.LeaseNotActiveException;
import com.kata.app.car.application.exception.LeaseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CarNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleCarNotFound(CarNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ApiErrorResponse("CAR_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ApiErrorResponse("CUSTOMER_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(LeaseNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleLeaseNotFound(LeaseNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ApiErrorResponse("LEASE_NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(CarAlreadyLeasedException.class)
	public ResponseEntity<ApiErrorResponse> handleCarAlreadyLeased(CarAlreadyLeasedException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ApiErrorResponse("CAR_ALREADY_LEASED", ex.getMessage()));
	}

	@ExceptionHandler(LeaseNotActiveException.class)
	public ResponseEntity<ApiErrorResponse> handleLeaseNotActive(LeaseNotActiveException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ApiErrorResponse("LEASE_NOT_ACTIVE", ex.getMessage()));
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
	public ResponseEntity<ApiErrorResponse> handleValidation(Exception ex) {
		String details;
		if (ex instanceof MethodArgumentNotValidException manve) {
			details = manve.getBindingResult().getFieldErrors().stream()
				.map(this::formatFieldError)
				.collect(Collectors.joining("; "));
			if (details.isEmpty()) {
				details = "Invalid request payload";
			}
		} else if (ex instanceof ConstraintViolationException cve) {
			details = cve.getConstraintViolations().stream()
				.map(v -> v.getPropertyPath() + ": " + v.getMessage())
				.collect(Collectors.joining("; "));
			if (details.isEmpty()) {
				details = "Constraint violation";
			}
		} else {
			details = "Validation error";
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ApiErrorResponse("VALIDATION_ERROR", details));
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, IllegalArgumentException.class })
	public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ApiErrorResponse("BAD_REQUEST", "Invalid request"));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ApiErrorResponse("ILLEGAL_STATE", "Operation not allowed in current state"));
	}

	private String formatFieldError(FieldError error) {
		return error.getField() + ": " + (error.getDefaultMessage() != null ? error.getDefaultMessage() : "invalid");
	}
}


