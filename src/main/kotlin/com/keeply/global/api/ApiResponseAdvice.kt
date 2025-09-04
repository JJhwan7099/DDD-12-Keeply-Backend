package com.keeply.global.api

import com.keeply.api.login.dto.LoginResponseDTO
import com.keeply.global.api.ApiResponse
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@Hidden
@RestControllerAdvice(basePackages = ["com.keeply.api"])
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
        return when (body){
            is ApiResponse<*> -> {
                response.setStatusCode(HttpStatus.valueOf(body.statusCode))
                if (request.uri.path == "/api/login" && body.success) {
                    (body.response as? LoginResponseDTO)?.let { data ->
                        response.headers.add("Authorization", "Bearer ${data.accessToken}")
                    }
                }
                body
            }
            else -> body
        }
    }
}