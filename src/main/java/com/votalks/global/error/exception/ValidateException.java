package com.votalks.global.error.exception;

import com.votalks.global.error.model.ErrorCode;

public class ValidateException extends BusinessException {
	public ValidateException(ErrorCode errorCode) {
		super(errorCode);
	}
}
