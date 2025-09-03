package com.keeply.global.exception.code.image

import com.keeply.global.exception.code.ErrorResultCode

enum class ErrorCode(
    override val message: String
): ErrorResultCode {
    IMAGE_NOT_FOUND("이미지를 찾을 수 없습니다."),
    IMAGE_SIZE_TOO_LARGE("이미지의 크기가 1MB를 초과합니다."),
    INVALID_IMAGE_ID("이미지 Id는 양의 정수입니다."),
    IMAGE_ILLEGAL_ARGUMENT("""
        isCached가 true일 경우 cachedImageId는 필수입니다.
        isCached가 false일 경우 imageId는 필수입니다.
    """)
}