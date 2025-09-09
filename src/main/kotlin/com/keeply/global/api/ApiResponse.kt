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
        /**
         * Creates a successful ApiResponse with the given HTTP status and optional payload.
         *
         * @param httpStatus HTTP status whose numeric value will be used for `statusCode`.
         * @param response Optional response payload to include in the `response` field.
         * @return An ApiResponse with `success = true`, `statusCode` set from `httpStatus`, `reason = null`, and the provided `response`.
         */
        fun <T> success(httpStatus: HttpStatus, response: T?): ApiResponse<T> {
            return ApiResponse(
                success = true,
                statusCode = httpStatus.value(),
                reason = null,
                response = response
            )
        }

        /**
         * Creates a successful ApiResponse with the given HTTP status and no response payload.
         *
         * @param httpStatus HTTP status to set on the response.
         * @return An ApiResponse with `success = true`, `statusCode` taken from the provided `httpStatus`, `reason = null`, and `response = null`.
         */
        fun <T> success(httpStatus: HttpStatus): ApiResponse<T> {
            return success(httpStatus, null)
        }

        /**
         * Creates a failure ApiResponse populated from the given ErrorResultCode.
         *
         * The returned response has `success` set from `errorResultCode.isSuccess()`, `statusCode`
         * from `errorResultCode.status.value()`, `reason` from `errorResultCode.message`, and a `null` payload.
         *
         * @param errorResultCode Source of the status, message, and success flag for the response.
         * @return An ApiResponse with no response payload (ApiResponse&lt;Nothing&gt;) representing the failure.
         */
        fun failure(errorResultCode: ErrorResultCode): ApiResponse<Nothing> {
            return ApiResponse(
                success = errorResultCode.isSuccess(),
                statusCode = errorResultCode.status.value(),
                reason = errorResultCode.message,
                response = null
            )
        }

        /**
         * Build a failure ApiResponse with the given HTTP status and human-readable reason.
         *
         * @param httpStatus HTTP status whose numeric value is used for the response's `statusCode`.
         * @param message Failure reason returned in the response's `reason` field.
         * @return An `ApiResponse<Nothing>` representing a failed response (success = false) with no payload.
         */
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