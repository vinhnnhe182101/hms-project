package com.product.hms.advice;

import com.product.hms.dto.response.ErrorResponse;
import com.product.hms.exception.ApiException;
import com.product.hms.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = buildErrorResponse(
                errorCode.code(),
                ex.getMessage(),
                errorCode.status(),
                request.getRequestURI()
        );
        return ResponseEntity.status(errorCode.status()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        ErrorResponse response = buildErrorResponse(
                ErrorCode.INVALID_REQUEST.code(),
                message,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception ex, HttpServletRequest request) {
        ErrorResponse response = buildErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.code(),
                "Unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private ErrorResponse buildErrorResponse(String code, String message, HttpStatus status, String path) {
        return new ErrorResponse(code, message, status.value(), path, Instant.now());
    }
}