package com.keeply.global.exception.code

interface SuccessResultCode {
    fun isSuccess() = true
    val message: String
}