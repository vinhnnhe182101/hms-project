package com.product.hms.utils.page.exception;

import com.product.hms.exception.ApiException;
import com.product.hms.exception.ErrorCode;

public class InvalidPageException extends ApiException {
    public InvalidPageException(String message) {
        super(ErrorCode.INVALID_PAGE_PARAMS, message);
    }

    public InvalidPageException(int index, int size) {
        super(ErrorCode.INVALID_PAGE_PARAMS,
                String.format("Invalid page index or size: %d, %d", index, size));
    }
}
