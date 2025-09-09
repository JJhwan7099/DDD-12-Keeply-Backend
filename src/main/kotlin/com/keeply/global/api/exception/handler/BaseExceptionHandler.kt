package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.code.ErrorResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

abstract class BaseExceptionHandler {
    fun buildErrorResponse(errorResultCode: ErrorResultCode): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(errorResultCode.status)
            .body(
                ApiResponse.failure(errorResultCode)
            )
    }

    fun buildErrorResponse(httpStatus: HttpStatus, message: String): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(httpStatus)
            .body(
                ApiResponse.failure(httpStatus, message)
            )
    }
}