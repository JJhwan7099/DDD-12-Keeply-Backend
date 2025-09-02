package com.keeply.global.exception.code.user

import com.keeply.global.exception.code.ErrorResultCode

enum class ErrorCode(
    override val message: String
): ErrorResultCode {
    USER_NOT_FOUND("유저를 찾을 수 없습니다.")
}