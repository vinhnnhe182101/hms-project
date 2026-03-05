package com.product.hms.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

