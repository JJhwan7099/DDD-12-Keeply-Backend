package com.keeply.global.api.exception.handler

import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.ocr.OcrIllegalArgumentException
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice(basePackages = ["com.keeply.api.ocr"])
class OcrExceptionHandler: BaseExceptionHandler() {
    /**
     * Handles OcrIllegalArgumentException thrown by OCR controllers and returns an HTTP error response.
     *
     * Converts the exception's ErrorResultCode into a standard ApiResponse error payload with no body.
     *
     * @param e The caught OcrIllegalArgumentException whose ErrorResultCode determines the response.
     * @return A ResponseEntity containing an ApiResponse with no payload describing the error.
     */
    @ExceptionHandler(OcrIllegalArgumentException::class)
    fun handleOcrIllegalArgumentException(e: OcrIllegalArgumentException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}