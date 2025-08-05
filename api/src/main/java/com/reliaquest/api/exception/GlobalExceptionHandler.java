package com.reliaquest.api.exception;

import com.reliaquest.api.model.DTO.ApiResponse;
import feign.RetryableException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        ErrorDetails errorDetails =
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));

        log.warn("Resource not found: {}", errorDetails);

        // Return a properly formatted ApiResponse with 404 status
        ApiResponse<Void> response = new ApiResponse<>(ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);

        ApiResponse<Map<String, String>> response = new ApiResponse<>("Validation failed", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RemoteServiceException.class)
    public final ResponseEntity<ApiResponse<ErrorDetails>> handleRemoteServiceException(
            RemoteServiceException ex, WebRequest request) {

        ErrorDetails errorDetails =
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        log.error("Error: " + ex.getMessage() + " details: " + errorDetails);
        return new ResponseEntity<>(
                new ApiResponse<>("Error: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ApiResponse<Object>> handleRetryableException(RetryableException ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.retryAfter()))
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse<ErrorDetails>> handleAllExceptions(Exception ex, WebRequest request) {

        ErrorDetails errorDetails =
                new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        log.error("Error: " + ex.getMessage() + " details: " + errorDetails);
        return new ResponseEntity<>(
                new ApiResponse<>("Error: " + ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class ErrorDetails {
        private final LocalDateTime timestamp;
        private final String message;
        private final String details;

        public ErrorDetails(LocalDateTime timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }

        // Getters
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getMessage() {
            return message;
        }

        public String getDetails() {
            return details;
        }
    }
}
