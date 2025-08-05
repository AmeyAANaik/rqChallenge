package com.reliaquest.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 * Results in an HTTP 404 Not Found response status.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new instance with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new instance with a formatted message including resource details.
     *
     * @param resourceName the name of the resource that was not found
     * @param fieldName the name of the field that was used for lookup
     * @param fieldValue the value of the field that was used for lookup
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
