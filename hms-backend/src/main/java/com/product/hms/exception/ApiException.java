package com.product.hms.exception;

import lombok.Getter;

/**
 * Base exception carrying standardized error code.
 */
@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}

