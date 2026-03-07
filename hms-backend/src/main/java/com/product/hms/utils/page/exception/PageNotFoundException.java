package com.product.hms.utils.page.exception;

import com.product.hms.exception.ApiException;
import com.product.hms.exception.ErrorCode;

public class PageNotFoundException extends ApiException {
    public PageNotFoundException(String message) {
        super(ErrorCode.PAGE_NOT_FOUND, message);
    }

    public PageNotFoundException(Class<?> entityClass, int pageIndex, int pageSize) {
        super(ErrorCode.PAGE_NOT_FOUND,
                String.format("Page not found for entity %s at index %d with size %d",
                        entityClass.getName(), pageIndex, pageSize));
    }
}
