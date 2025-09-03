package com.keeply.global.exception.code

import org.springframework.http.HttpStatus

interface SuccessResultCode {
    fun isSuccess() = true
    val status: HttpStatus
}