package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.user.InvalidUserIdException
import com.keeply.global.api.exception.user.UserNotFoundException
import com.keeply.global.api.exception.user.UserSettingNotFoundException
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice(basePackages = ["com.keeply.api.user"])
class UserExceptionHandler: BaseExceptionHandler() {

    /**
     * Handles a UserNotFoundException and converts it into a standardized API error response.
     *
     * Returns a ResponseEntity containing an ApiResponse with no payload and the error result code taken from the exception.
     *
     * @return ResponseEntity<ApiResponse<Nothing>> an error response built from the exception's `errorResultCode`.
     */
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(e: UserNotFoundException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    /**
     * Handles InvalidUserIdException and returns a standardized error response.
     *
     * Builds an HTTP error ResponseEntity containing an ApiResponse with no body,
     * using the exception's `errorResultCode` to determine the payload and status.
     *
     * @param e The thrown InvalidUserIdException (provides `errorResultCode`).
     * @return ResponseEntity containing an ApiResponse<Nothing> representing the error.
     */
    @ExceptionHandler(InvalidUserIdException::class)
    fun handleInvalidUserIdException(e: InvalidUserIdException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    /**
     * Handles a missing user setting error and returns a standardized API error response.
     *
     * @param e The UserSettingNotFoundException whose `errorResultCode` is used to construct the response.
     * @return A ResponseEntity containing an ApiResponse with no body and the corresponding error information.
     */
    @ExceptionHandler(UserSettingNotFoundException::class)
    fun handleUserSettingNotFoundException(e: UserSettingNotFoundException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}