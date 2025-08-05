package com.reliaquest.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client has sent too many requests in a given amount of time.
 * Maps to HTTP status code 429 - Too Many Requests.
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends RuntimeException {
    private long retryAfter = 1; // Default to 1 second

    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException(String message, long retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }

    public long getRetryAfter() {
        return retryAfter;
    }
}
