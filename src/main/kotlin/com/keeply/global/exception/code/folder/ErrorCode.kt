package com.keeply.global.exception.code.folder

import com.keeply.global.exception.code.ErrorResultCode

enum class ErrorCode(
    override val message: String
): ErrorResultCode {
    FOLDER_NOT_FOUND("폴더를 찾을 수 없습니다.")
}