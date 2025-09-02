package com.keeply.global.exception.code.image

import com.keeply.global.exception.code.ErrorResultCode

enum class ErrorCode(
    override val message: String
): ErrorResultCode {
    IMAGE_NOT_FOUND("이미지를 찾을 수 없습니다."),
    IMAGE_SIZE_TOO_LARGE("이미지의 크기가 1MB를 초과합니다.")
}