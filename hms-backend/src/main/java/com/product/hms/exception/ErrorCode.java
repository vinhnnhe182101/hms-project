package com.product.hms.exception;

import org.springframework.http.HttpStatus;

/**
 * Centralized error codes for API exception mapping.
 * Organized by domain for easy navigation and future expansion.
 *
 * @since 1.0
 */
public enum ErrorCode {
    // Generic / Common Errors
    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    // Customer Domain
    CUSTOMER_NOT_FOUND("CUSTOMER_NOT_FOUND", HttpStatus.NOT_FOUND),
    CUSTOMER_INACTIVE("CUSTOMER_INACTIVE", HttpStatus.CONFLICT),
    DUPLICATE_IDENTITY_CARD("DUPLICATE_IDENTITY_CARD", HttpStatus.CONFLICT),

    // Room Domain
    ROOM_NOT_FOUND("ROOM_NOT_FOUND", HttpStatus.NOT_FOUND),
    ROOM_INACTIVE("ROOM_INACTIVE", HttpStatus.CONFLICT),
    ROOM_CLASS_NOT_FOUND("ROOM_CLASS_NOT_FOUND", HttpStatus.NOT_FOUND),
    ROOM_CLASS_INACTIVE("ROOM_CLASS_INACTIVE", HttpStatus.CONFLICT),
    INSUFFICIENT_AVAILABLE_ROOMS("INSUFFICIENT_AVAILABLE_ROOMS", HttpStatus.CONFLICT),
    EXCEED_MAX_CAPACITY("EXCEED_MAX_CAPACITY", HttpStatus.BAD_REQUEST),

    // Reservation Domain
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", HttpStatus.NOT_FOUND),
    INVALID_DATE_RANGE("INVALID_DATE_RANGE", HttpStatus.BAD_REQUEST),
    RESERVATION_UPDATE_LOCKED("RESERVATION_UPDATE_LOCKED", HttpStatus.CONFLICT),
    RESERVATION_ALREADY_CANCELED("RESERVATION_ALREADY_CANCELED", HttpStatus.CONFLICT),
    RESERVATION_CANCEL_NOT_ALLOWED("RESERVATION_CANCEL_NOT_ALLOWED", HttpStatus.CONFLICT),

    // Utilities / Technical Errors
    INVALID_PAGE_PARAMS("INVALID_PAGE_PARAMS", HttpStatus.BAD_REQUEST),
    PAGE_NOT_FOUND("PAGE_NOT_FOUND", HttpStatus.NOT_FOUND),
    RESOURCE_UNSUPPORTED("RESOURCE_UNSUPPORTED", HttpStatus.BAD_REQUEST),
    MAPPING_ERROR("MAPPING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus status;

    ErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    /**
     * Returns the error code string.
     */
    public String code() {
        return code;
    }

    /**
     * Returns the HTTP status associated with this error code.
     */
    public HttpStatus status() {
        return status;
    }
}
