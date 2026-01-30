package com.example.userpaymentservice.exception.base;

/**
 * Interface representing a service that provides error details.
 * Includes methods for retrieving the error message, HTTP status code, and error code.
 */
public interface BaseErrorService {

    /**
     * Gets the error message.
     *
     * @return The error message.
     */
    String getMessage();

    /**
     * Gets the HTTP status code associated with the error.
     *
     * @return The HTTP status code.
     */
    int getHttpStatus();

    /**
     * Gets the unique error code.
     *
     * @return The error code.
     */
    String getErrorCode();
}

