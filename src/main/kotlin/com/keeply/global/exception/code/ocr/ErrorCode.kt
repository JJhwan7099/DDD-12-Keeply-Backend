package com.keeply.global.exception.code.ocr

import com.keeply.global.exception.code.ErrorResultCode

enum class ErrorCode(
    override val message: String
): ErrorResultCode {
    OCR_ILLEGAL_ARGUMENT("""
        isNew가 true인 경우 imageId는 null이어야 합니다.
        isNew가 false인 경우 imageId는 필수입니다.
    """.trimIndent())
}