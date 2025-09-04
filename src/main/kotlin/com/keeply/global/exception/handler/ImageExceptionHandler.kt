package com.keeply.global.exception.handler

import com.keeply.global.api.dto.ApiResponse
import com.keeply.global.exception.image.ImageIllegalArgumentException
import com.keeply.global.exception.image.ImageNotFoundException
import com.keeply.global.exception.image.ImageSizeTooLargeException
import com.keeply.global.exception.image.InvalidImageIdException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.keeply.api.image"])
class ImageExceptionHandler: BaseExceptionHandler() {
    @ExceptionHandler(ImageNotFoundException::class)
    fun handleImageNotFoundException(e: ImageNotFoundException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    @ExceptionHandler(ImageIllegalArgumentException::class)
    fun handleImageIllegalArgumentException(e: ImageIllegalArgumentException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    @ExceptionHandler(ImageSizeTooLargeException::class)
    fun handleImageSizeTooLargeException(e: ImageSizeTooLargeException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }

    @ExceptionHandler(InvalidImageIdException::class)
    fun handleInvalidImageIdException(e: InvalidImageIdException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}