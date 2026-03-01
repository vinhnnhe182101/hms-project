package com.product.hms.utils.page.exception;

public class InvalidPageException extends RuntimeException {
    public InvalidPageException(String message) {
        super(message);
    }

    public InvalidPageException(int index, int size) {
        super(String.format("Invalid page index or size: %d, %d", index, size));
    }
}
