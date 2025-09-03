package com.keeply.global.exception.handler

import com.keeply.global.dto.ApiResponse
import com.keeply.global.exception.code.ErrorResultCode
import org.springframework.http.ResponseEntity

abstract class BaseExceptionHandler {
    fun buildErrorResponse(errorResultCode: ErrorResultCode): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(errorResultCode.status)
            .body(
                ApiResponse.failure(errorResultCode)
            )
    }
}