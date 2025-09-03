package com.keeply.global.exception.code

import org.springframework.http.HttpStatus

interface ErrorResultCode {
    fun isSuccess() = false
    val message: String
    val status: HttpStatus
}