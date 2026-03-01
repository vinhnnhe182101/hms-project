package com.product.hms.utils.page.exception;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(String message) {
        super(message);
    }

    public PageNotFoundException(Class<?> entityClass, int pageIndex, int pageSize) {
        super(String.format("Page not found for entity %s at index %d with size %d",
                entityClass.getName(), pageIndex, pageSize));
    }
}
