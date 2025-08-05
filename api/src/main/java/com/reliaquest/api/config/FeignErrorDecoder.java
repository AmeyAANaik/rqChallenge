package com.reliaquest.api.config;

import com.reliaquest.api.exception.ResourceNotFoundException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Custom error decoder for Feign client to handle specific HTTP status codes
 * and convert them to appropriate exceptions.
 */
@Slf4j
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        String errorMessage =
                String.format("Error occurred while calling %s: %s - %s", methodKey, response.status(), requestUrl);

        switch (response.status()) {
            case 404:
                log.warn("Resource not found: {}", requestUrl);
                // Return a custom exception that will be handled by GlobalExceptionHandler
                return new ResourceNotFoundException("Employee not found with the given ID");

            case 429: // Too Many Requests
                log.warn("Rate limit exceeded for: {}", requestUrl);
                return new RetryableException(
                        response.status(),
                        "Rate limit exceeded. Will retry...",
                        response.request().httpMethod(),
                        Date.from(new Date().toInstant().plusSeconds(20)) ,
                        response.request());
            case 500:
                log.error("Internal server error for: {}", requestUrl);
                break;

            case 503:
                log.error("Service unavailable: {}", requestUrl);
                return new RetryableException(
                        response.status(),
                        "Service unavailable",
                        response.request().httpMethod(),
                        Date.from(new Date().toInstant()),
                        response.request());

            default:
                log.error("{} - {}", response.status(), errorMessage);
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}
