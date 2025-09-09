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
    @ExceptionHandler(OcrIllegalArgumentException::class)
    fun handleOcrIllegalArgumentException(e: OcrIllegalArgumentException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}