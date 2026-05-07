package com.onghub.api.exception;

import com.onghub.api.dto.response.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage(), request, HttpStatus.NOT_FOUND, List.of());
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateEntryException ex, HttpServletRequest request) {
        return buildError(ErrorCode.DUPLICATE_ENTRY, ex.getMessage(), request, HttpStatus.CONFLICT, List.of());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        return buildError(ErrorCode.INVALID_CREDENTIALS, ex.getMessage(), request, HttpStatus.UNAUTHORIZED, List.of());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpired(TokenExpiredException ex, HttpServletRequest request) {
        return buildError(ErrorCode.TOKEN_EXPIRED, ex.getMessage(), request, HttpStatus.UNAUTHORIZED, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(this::formatFieldError)
            .toList();
        return buildError(ErrorCode.VALIDATION_ERROR, "Validation error", request, HttpStatus.BAD_REQUEST, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildError(ErrorCode.INTERNAL_ERROR, "Unexpected error", request, HttpStatus.INTERNAL_SERVER_ERROR, List.of());
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ApiError> handleInvalidStatusTransition(InvalidStatusTransitionException ex, HttpServletRequest request) {
        return buildError(
            ErrorCode.INVALID_STATUS_TRANSITION,
            ex.getMessage(),
            request,
            HttpStatus.BAD_REQUEST,
            List.of()
        );
    }

    private ResponseEntity<ApiError> buildError(
        ErrorCode code,
        String message,
        HttpServletRequest request,
        HttpStatus status,
        List<String> details
    ) {
        ApiError error = new ApiError(
            code.name(),
            message,
            details,
            Instant.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
