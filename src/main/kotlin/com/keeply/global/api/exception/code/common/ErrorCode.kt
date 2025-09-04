package com.keeply.global.api.exception.code.common

import com.keeply.global.api.exception.code.ErrorResultCode
import org.springframework.http.HttpStatus

enum class ErrorCode(
    override val status: HttpStatus,
    override val message: String
): ErrorResultCode {
    INVALID_REQUEST_CONTENT(HttpStatus.BAD_REQUEST, "올바르지 않은 요청 데이터입니다."),
    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, "필수 요청값이 존재하지 않습니다."),
    INVALID_REQUEST_PATH(HttpStatus.NOT_FOUND, "올바르지 않은 요청 경로입니다."),
    INVALID_HTTP_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "올바르지 않은 HTTP 메서드입니다.");
}