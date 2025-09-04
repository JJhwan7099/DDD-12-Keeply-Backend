package com.keeply.global.api.exception.code.folder

import com.keeply.global.api.exception.code.ErrorResultCode
import org.springframework.http.HttpStatus

enum class ErrorCode(
    override val status: HttpStatus,
    override val message: String
): ErrorResultCode {
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다."),
    INVALID_FOLDER_ID(HttpStatus.BAD_REQUEST, "폴더 Id는 양의 정수입니다.")
}