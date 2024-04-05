package com.votalks.global.error.exception;

import com.votalks.global.error.model.ErrorCode;

public class BadRequestException extends VotalksException {
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
