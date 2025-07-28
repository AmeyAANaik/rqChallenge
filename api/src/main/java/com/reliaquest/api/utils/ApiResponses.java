package com.reliaquest.api.utils;

import com.reliaquest.api.exception.RemoteServiceException;
import com.reliaquest.api.model.DTO.ApiResponse;

public final class ApiResponses {
    private static final String SUCCESS_STATUS = "Successfully processed request.";

    private ApiResponses() {}

    public static <T> T unwrap(ApiResponse<T> response) {
        if (response == null) {
            throw new RemoteServiceException("Received null response from remote service");
        }
        if (!SUCCESS_STATUS.equalsIgnoreCase(response.getStatus())) {
            throw new RemoteServiceException(String.format("Remote service error: %s", response.getStatus()));
        }
        if (response.getData() == null) {
            throw new RemoteServiceException("Response data is null");
        }
        return response.getData();
    }
}
