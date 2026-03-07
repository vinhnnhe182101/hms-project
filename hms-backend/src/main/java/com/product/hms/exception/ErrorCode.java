package com.product.hms.exception;

import org.springframework.http.HttpStatus;

/**
 * Centralized error codes for API exception mapping.
 */
public enum ErrorCode {
    // Generic errors
    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    // Customer domain
    CUSTOMER_NOT_FOUND("CUSTOMER_NOT_FOUND", HttpStatus.NOT_FOUND),
    CUSTOMER_INACTIVE("CUSTOMER_INACTIVE", HttpStatus.CONFLICT),
    DUPLICATE_IDENTITY_CARD("DUPLICATE_IDENTITY_CARD", HttpStatus.CONFLICT),

    // Room domain
    ROOM_NOT_FOUND("ROOM_NOT_FOUND", HttpStatus.NOT_FOUND),
    ROOM_INACTIVE("ROOM_INACTIVE", HttpStatus.CONFLICT),
    ROOM_CLASS_NOT_FOUND("ROOM_CLASS_NOT_FOUND", HttpStatus.NOT_FOUND),
    ROOM_CLASS_INACTIVE("ROOM_CLASS_INACTIVE", HttpStatus.CONFLICT),
    INSUFFICIENT_AVAILABLE_ROOMS("INSUFFICIENT_AVAILABLE_ROOMS", HttpStatus.CONFLICT),

    // Reservation domain
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", HttpStatus.NOT_FOUND),
    INVALID_DATE_RANGE("INVALID_DATE_RANGE", HttpStatus.BAD_REQUEST),

    // User / Staff domain
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND),
    STAFF_NOT_FOUND("STAFF_NOT_FOUND", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", HttpStatus.CONFLICT),

    // Utils/Technical errors
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

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }
}

