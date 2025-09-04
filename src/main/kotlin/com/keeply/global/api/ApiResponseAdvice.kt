package com.keeply.global.api

import com.keeply.api.login.dto.LoginResponseDTO
import com.keeply.global.api.dto.ApiResponse
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
        val path = request.uri.path

        return when {
            path == "/api/login" && body is ApiResponse<*> -> {

                val data = body.response as LoginResponseDTO

                ResponseEntity
                    .status(body.statusCode)
                    .header("Authorization", "Bearer ${data.accessToken}")
                    .body(body)
            }

            body is ApiResponse<*> -> ResponseEntity
                .status(body.statusCode)
                .body(body)

            else -> body
        }
    }
}