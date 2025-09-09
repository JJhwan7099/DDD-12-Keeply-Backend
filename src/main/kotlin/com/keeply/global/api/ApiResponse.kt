package com.keeply.global.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.keeply.global.api.exception.code.ErrorResultCode
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val statusCode: Int,
    val reason: String? = null,
    val response: T? = null
) {
    companion object {
        fun <T> success(httpStatus: HttpStatus, response: T?): ApiResponse<T> {
            return ApiResponse(
                success = true,
                statusCode = httpStatus.value(),
                reason = null,
                response = response
            )
        }

        fun <T> success(httpStatus: HttpStatus): ApiResponse<T> {
            return success(httpStatus, null)
        }

        fun failure(errorResultCode: ErrorResultCode): ApiResponse<Nothing> {
            return ApiResponse(
                success = errorResultCode.isSuccess(),
                statusCode = errorResultCode.status.value(),
                reason = errorResultCode.message,
                response = null
            )
        }

        fun failure(httpStatus: HttpStatus, message: String): ApiResponse<Nothing> {
            return ApiResponse(
                success = false,
                statusCode = httpStatus.value(),
                reason = message,
                response = null
            )
        }
    }
}