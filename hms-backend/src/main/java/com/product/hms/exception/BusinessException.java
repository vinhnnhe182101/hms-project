package com.product.hms.exception;

public class BusinessException extends RuntimeException {
    private ErrorCode errorCode = null;

    public BusinessException(String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
