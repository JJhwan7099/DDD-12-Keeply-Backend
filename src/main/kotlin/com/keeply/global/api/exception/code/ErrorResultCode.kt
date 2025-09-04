package com.keeply.global.api.exception.code

import org.springframework.http.HttpStatus

interface ErrorResultCode {
    fun isSuccess() = false
    val status: HttpStatus
    val message: String
}