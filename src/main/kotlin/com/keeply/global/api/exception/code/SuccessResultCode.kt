package com.keeply.global.api.exception.code

import org.springframework.http.HttpStatus

interface SuccessResultCode {
    fun isSuccess() = true
    val status: HttpStatus
}