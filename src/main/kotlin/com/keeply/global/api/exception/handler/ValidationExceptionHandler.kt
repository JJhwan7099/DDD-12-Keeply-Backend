package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.*

@Hidden
@RestControllerAdvice(basePackages = ["com.keeply.api"])
class ValidationExceptionHandler(
    private val messageSource: MessageSource
): BaseExceptionHandler() {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<ApiResponse<Nothing>>{
        val errors = e.bindingResult.fieldErrors.map {
            fieldError ->
            messageSource.getMessage(fieldError, Locale.getDefault())
        }
        val message = errors.joinToString(", ")
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        e: ConstraintViolationException
    ): ResponseEntity<ApiResponse<Nothing>>{
        val errors = e.constraintViolations.map {
            violation ->
            val property = violation.propertyPath.toString().split(".").last()
            val message = violation.message
            "$property: $message"
        }
        val message = errors.joinToString(", ")
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message)
    }
}