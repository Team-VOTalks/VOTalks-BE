package com.votalks.global.error.exception;

import com.votalks.global.error.model.ErrorCode;

public class BusinessException extends RuntimeException {
	private ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
