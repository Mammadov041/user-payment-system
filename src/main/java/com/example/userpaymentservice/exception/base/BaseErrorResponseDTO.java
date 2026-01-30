package com.example.userpaymentservice.exception.base;

/**
 * Data transfer object representing an error response.
 * Contains information about the error such as code, message, and path,
 * along with optional additional data.
 *
 * @param code The unique error code.
 * @param message   The error message.
 * @param path      The path where the error occurred.
 * @param timestamp Timestamp
 * @param data      Additional data related to the error (optional).
 */

public record BaseErrorResponseDTO(String code, String message, String path, String timestamp,
                                   Integer status, Object... data) {
}

