package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.*

@RestControllerAdvice(basePackages = ["com.keeply.api"])
class ValidationExceptionHandler(
    private val messageSource: MessageSource
): BaseExceptionHandler() {

    /**
     * Handles MethodArgumentNotValidException thrown for @Valid request body/parameters and returns a 400 Bad Request response.
     *
     * Localizes each field error using the injected MessageSource, joins them into a single comma-separated message,
     * and wraps it in an ApiResponse returned with HttpStatus.BAD_REQUEST.
     *
     * @param e The MethodArgumentNotValidException containing field validation errors.
     * @return ResponseEntity containing an ApiResponse with the combined localized validation message and HTTP 400 status.
     */
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

    /**
     * Handles jakarta.validation.ConstraintViolationException and converts validation violations
     * into a single BAD_REQUEST ApiResponse.
     *
     * Each constraint violation is converted to the format "`property: message`", where
     * `property` is the last segment of the constraint's property path. All entries are
     * joined with ", " and returned as the response message.
     *
     * @param e The ConstraintViolationException containing the validation violations.
     * @return ResponseEntity containing an ApiResponse with HTTP 400 (Bad Request) and a
     *         combined validation message.
     */
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