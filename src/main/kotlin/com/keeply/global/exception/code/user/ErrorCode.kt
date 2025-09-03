package com.keeply.global.exception.code.user

import com.keeply.global.exception.code.ErrorResultCode
import org.springframework.http.HttpStatus

enum class ErrorCode(
    override val status: HttpStatus,
    override val message: String
): ErrorResultCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유저 ID는 양의 정수입니다."),
    USER_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "유저의 설정 정보를 찾을 수 없습니다.")
}