package com.keeply.global.util

import com.keeply.global.api.dto.ApiResponse
import com.keeply.global.api.dto.Message
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice(basePackages = ["com.keeply.api"])
class GlobalRestControllerAdvice {
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Message>> {
        if (e.stackTrace.any { it.className.contains("springdoc") }) {
            throw e
        }
        val apiResponse = ApiResponse<Message>(
            success = false,
            reason = e.message
        )
        return ResponseEntity.badRequest().body(apiResponse)
    }
}