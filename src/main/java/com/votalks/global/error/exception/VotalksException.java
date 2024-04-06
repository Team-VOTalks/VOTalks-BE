package com.votalks.global.error.exception;

import com.votalks.global.error.model.ErrorCode;

public class VotalksException extends RuntimeException {
	private ErrorCode errorCode;

	public VotalksException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
