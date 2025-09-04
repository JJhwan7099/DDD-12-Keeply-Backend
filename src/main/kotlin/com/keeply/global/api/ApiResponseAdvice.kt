package com.keeply.global.api

import com.keeply.global.api.dto.ApiResponse
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class ApiResponseAdvice: ResponseBodyAdvice<Any>{
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>?>
    ): Boolean {
        val parameterType = returnType.parameterType
        return ApiResponse::class.java.isAssignableFrom(parameterType)
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>?>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        return when (body) {
            is ApiResponse<*> -> {
                    ResponseEntity
                        .status(body.status)
                        .body(body)
                }
            else -> body
        }
    }
}