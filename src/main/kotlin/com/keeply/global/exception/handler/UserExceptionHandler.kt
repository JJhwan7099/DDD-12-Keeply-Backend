package com.keeply.global.exception.handler

import com.keeply.global.dto.ApiResponse
import com.keeply.global.exception.user.InvalidUserIdException
import com.keeply.global.exception.user.UserNotFoundException
import com.keeply.global.exception.user.UserSettingNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.keeply.api.user"])
class UserExceptionHandler: BaseExceptionHandler() {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(e: UserNotFoundException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    @ExceptionHandler(InvalidUserIdException::class)
    fun handleInvalidUserIdException(e: InvalidUserIdException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    @ExceptionHandler(UserSettingNotFoundException::class)
    fun handleUserSettingNotFoundException(e: UserSettingNotFoundException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}