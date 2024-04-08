package com.votalks.global.error.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.votalks.global.error.exception.BadRequestException;
import com.votalks.global.error.exception.NotFoundException;
import com.votalks.global.error.model.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(NotFoundException.class)
	protected ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
		ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	protected ResponseEntity<Object> handleValidateException(BadRequestException e) {
		ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		String errorMessage = fieldErrors.get(0).getDefaultMessage();
		ErrorResponse errorResponse = new ErrorResponse(errorMessage);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
