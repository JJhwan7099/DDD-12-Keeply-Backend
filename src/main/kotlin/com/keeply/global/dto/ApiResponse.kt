package com.keeply.global.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.keeply.global.exception.code.ErrorResultCode
import com.keeply.global.exception.code.SuccessResultCode
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val status: HttpStatus,
    val reason: String? = null,
    val response: T? = null
) {
    companion object {
        fun <T> success(successResultCode: SuccessResultCode, response: T?): ApiResponse<T> {
            return ApiResponse(
                success = successResultCode.isSuccess(),
                status = HttpStatus.OK,
                reason = null,
                response = response
            )
        }

        fun <T> success(successResultCode: SuccessResultCode): ApiResponse<T> {
            return success(successResultCode, null)
        }

        fun failure(errorResultCode: ErrorResultCode): ApiResponse<Nothing> {
            return ApiResponse(
                success = errorResultCode.isSuccess(),
                status = errorResultCode.status,
                reason = errorResultCode.message,
                response = null
            )
        }
    }
}
