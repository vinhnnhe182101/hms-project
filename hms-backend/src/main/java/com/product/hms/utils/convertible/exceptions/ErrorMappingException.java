package com.product.hms.utils.convertible.exceptions;

import com.product.hms.exception.ApiException;
import com.product.hms.exception.ErrorCode;

public class ErrorMappingException extends ApiException {
    public ErrorMappingException(Class<?> sourceClass, Class<?> targetClass) {
        super(ErrorCode.MAPPING_ERROR,
                String.format("Error mapping from %s to %s", sourceClass.getSimpleName(), targetClass.getSimpleName()));
    }
}
