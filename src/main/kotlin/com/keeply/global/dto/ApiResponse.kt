package com.keeply.global.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.keeply.global.exception.code.ErrorResultCode
import com.keeply.global.exception.code.SuccessResultCode

data class ApiResponse<T>(
    val success: Boolean,
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    val reason: String? = null,
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    val response: T? = null
) {
    fun success(successResultCode: SuccessResultCode, response: T?) : ApiResponse<T>{
        return ApiResponse(
            success = successResultCode.isSuccess(),
            reason = null,
            response = response
        )
    }

    fun success(successResultCode: SuccessResultCode): ApiResponse<T> {
        return success(successResultCode, null)
    }

    fun failure(errorResultCode: ErrorResultCode): ApiResponse<T> {
        return ApiResponse(
            success = errorResultCode.isSuccess(),
            reason = errorResultCode.message,
            response = null
        )
    }
}