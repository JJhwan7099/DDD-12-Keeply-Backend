package com.keeply.global.exception.code.user

import com.keeply.global.exception.code.ErrorResultCode

enum class ErrorCode(
    override val message: String
): ErrorResultCode {
    USER_NOT_FOUND("유저를 찾을 수 없습니다."),
    INVALID_USER_ID("유저 ID는 양의 정수입니다."),
    USER_SETTING_NOT_FOUND("유저의 설정 정보를 찾을 수 없습니다.")
}