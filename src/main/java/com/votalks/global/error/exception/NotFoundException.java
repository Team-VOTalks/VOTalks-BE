package com.votalks.global.error.exception;

import com.votalks.global.error.model.ErrorCode;

public class NotFoundException extends BusinessException {
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
