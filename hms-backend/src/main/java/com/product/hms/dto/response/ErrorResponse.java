package com.product.hms.dto.response;

import java.time.Instant;

/**
 * Standard error response for all APIs.
 *
 * @param code      business/system error code
 * @param message   error detail
 * @param status    HTTP status code
 * @param path      request path
 * @param timestamp response creation time
 */
public record ErrorResponse(
        String code,
        String message,
        int status,
        String path,
        Instant timestamp
) {
}

