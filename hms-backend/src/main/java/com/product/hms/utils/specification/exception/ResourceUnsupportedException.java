package com.product.hms.utils.specification.exception;

import com.product.hms.exception.ApiException;
import com.product.hms.exception.ErrorCode;

public class ResourceUnsupportedException extends ApiException {
    public ResourceUnsupportedException(String message) {
        super(ErrorCode.RESOURCE_UNSUPPORTED, message);
    }
}