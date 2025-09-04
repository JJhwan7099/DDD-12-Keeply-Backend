package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.code.ErrorResultCode
import org.springframework.http.ResponseEntity

abstract class BaseExceptionHandler {
    fun buildErrorResponse(errorResultCode: ErrorResultCode): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(errorResultCode.status)
            .body(
                ApiResponse.failure(errorResultCode)
            )
    }
}