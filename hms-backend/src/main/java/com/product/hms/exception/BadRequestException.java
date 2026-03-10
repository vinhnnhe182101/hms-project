package com.product.hms.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

