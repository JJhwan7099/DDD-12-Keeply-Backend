package com.keeply.global.exception.code.common

import com.keeply.global.exception.code.ErrorResultCode

enum class ErrorCode(
    override val message: String
): ErrorResultCode {
    INVALID_REQUEST_CONTENT("올바르지 않은 요청 데이터입니다."),
    MISSING_REQUIRED_PARAMETER("필수 요청값이 존재하지 않습니다."),
    INVALID_REQUEST_PATH("올바르지 않은 요청 경로입니다."),
    INVALID_HTTP_METHOD("올바르지 않은 HTTP 메서드입니다.");
}