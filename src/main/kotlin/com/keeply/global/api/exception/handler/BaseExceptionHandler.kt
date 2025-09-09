package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.code.ErrorResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

abstract class BaseExceptionHandler {
    /**
     * Creates an error HTTP response using the provided ErrorResultCode.
     *
     * @param errorResultCode ErrorResultCode whose HTTP status and error details will be used to build the response body.
     * @return ResponseEntity containing an ApiResponse failure payload and the status from the given ErrorResultCode.
     */
    fun buildErrorResponse(errorResultCode: ErrorResultCode): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(errorResultCode.status)
            .body(
                ApiResponse.failure(errorResultCode)
            )
    }

    /**
     * Build an error HTTP response with the given status and message.
     *
     * @param httpStatus HTTP status to use for the response.
     * @param message Human-readable error message to include in the response body.
     * @return ResponseEntity containing an `ApiResponse.failure` body with no data.
     */
    fun buildErrorResponse(httpStatus: HttpStatus, message: String): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(httpStatus)
            .body(
                ApiResponse.failure(httpStatus, message)
            )
    }
}