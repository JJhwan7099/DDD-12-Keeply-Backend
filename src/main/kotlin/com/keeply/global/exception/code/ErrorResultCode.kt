package com.keeply.global.exception.code

interface ErrorResultCode {
    fun isSuccess() = false
    val message: String
}