package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.image.ImageIllegalArgumentException
import com.keeply.global.api.exception.image.ImageNotFoundException
import com.keeply.global.api.exception.image.ImageSizeTooLargeException
import com.keeply.global.api.exception.image.InvalidImageIdException
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice(basePackages = ["com.keeply.api.image"])
class ImageExceptionHandler: BaseExceptionHandler() {
    /**
     * Handles ImageNotFoundException by converting it into a standardized error response.
     *
     * @param e The thrown ImageNotFoundException; its `errorResultCode` is used to build the response.
     * @return A ResponseEntity wrapping an ApiResponse with no body (Nothing) and the HTTP status/error payload derived from the exception's `errorResultCode`.
     */
    @ExceptionHandler(ImageNotFoundException::class)
    fun handleImageNotFoundException(e: ImageNotFoundException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    /**
     * Handles ImageIllegalArgumentException and converts it to a standardized error response.
     *
     * @param e The thrown exception; its `errorResultCode` is used to build the response.
     * @return A ResponseEntity containing an ApiResponse<Nothing> representing the error. 
     */
    @ExceptionHandler(ImageIllegalArgumentException::class)
    fun handleImageIllegalArgumentException(e: ImageIllegalArgumentException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    /**
     * Handles ImageSizeTooLargeException and returns a standardized error response.
     *
     * Uses the exception's `errorResultCode` to build a ResponseEntity containing an ApiResponse with no body.
     *
     * @param e The ImageSizeTooLargeException that was thrown.
     * @return ResponseEntity<ApiResponse<Nothing>> representing the error response.
     */
    @ExceptionHandler(ImageSizeTooLargeException::class)
    fun handleImageSizeTooLargeException(e: ImageSizeTooLargeException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    /**
     * Handles InvalidImageIdException and returns a standardized error response.
     *
     * Delegates to [buildErrorResponse] using the exception's `errorResultCode` to produce
     * a ResponseEntity containing an ApiResponse with no payload.
     *
     * @param e The thrown InvalidImageIdException whose `errorResultCode` determines the response.
     * @return ResponseEntity containing an ApiResponse<Nothing> representing the error.
     */
    @ExceptionHandler(InvalidImageIdException::class)
    fun handleInvalidImageIdException(e: InvalidImageIdException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}