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
    INVALID_REQUEST(),
    INTERNAL_SERVER_ERROR(),

    // Customer Domain
    CUSTOMER_NOT_FOUND(),
    CUSTOMER_INACTIVE(),
    DUPLICATE_IDENTITY_CARD(),

    // Room Domain
    ROOM_NOT_FOUND(),
    ROOM_INACTIVE(),
    ROOM_CLASS_NOT_FOUND(),
    ROOM_CLASS_INACTIVE(),
    INSUFFICIENT_AVAILABLE_ROOMS(),
    EXCEED_MAX_CAPACITY(),

    // Reservation Domain
    RESERVATION_NOT_FOUND(),
    INVALID_DATE_RANGE(),
    RESERVATION_UPDATE_LOCKED(),
    RESERVATION_ALREADY_CANCELED(),
    RESERVATION_CANCEL_NOT_ALLOWED(),

    // Utilities / Technical Errors
    INVALID_PAGE_PARAMS(),
    PAGE_NOT_FOUND(),
    RESOURCE_UNSUPPORTED(),
    MAPPING_ERROR(),

    // User related errors
    USER_NOT_FOUND(),
    DUPLICATE_EMAIL(),

    // Staff related errors
    STAFF_NOT_FOUND(), RESERVATION_ROOM_ASSIGNMENT_REQUIRED(), ROOM_CLASS_MISMATCH, ROOM_NOT_AVAILABLE, RESERVATION_ROOM_NOT_FOUND, RESERVATION_CHECKOUT_NOT_ALLOWED, RESERVATION_ROOM_NOT_CHECKED_IN, RESERVATION_ROOM_HAS_PENDING_SERVICES, SHIFT_NOT_FOUND, SERVICE_BOOKING_NOT_ALLOWED, SERVICE_BOOKING_NOT_FOUND, SERVICE_INACTIVE, SERVICE_NOT_FOUND, RESERVATION_CHECKIN_NOT_ALLOWED, INSUFFICIENT_DEPOSIT, FOLIO_ALREADY_CLOSED;

    private String code = "";
    private HttpStatus status;

    ErrorCode() {
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

    public enum RESERVATION_ROOM_ASSIGNMENT_REQUIRED {}
}
