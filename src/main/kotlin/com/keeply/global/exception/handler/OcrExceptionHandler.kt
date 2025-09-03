package com.keeply.global.exception.handler

import com.keeply.global.dto.ApiResponse
import com.keeply.global.exception.ocr.OcrIllegalArgumentException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.keeply.api.ocr"])
class OcrExceptionHandler: BaseExceptionHandler() {
    @ExceptionHandler(OcrIllegalArgumentException::class)
    fun handleOcrIllegalArgumentException(e: OcrIllegalArgumentException)
    : ResponseEntity<ApiResponse<Nothing>> {
        return buildErrorResponse(e.errorResultCode)
    }
}